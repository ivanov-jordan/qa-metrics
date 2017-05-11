package test.selenium.metrics;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import test.selenium.metrics.model.Resource;
import test.selenium.metrics.model.Result;
import test.selenium.metrics.util.ProxyFactory;
import test.selenium.metrics.util.TestSettings;
import test.selenium.metrics.util.WebDriverFactory;
import test.selenium.metrics.util.WebDriverType;

public class MetricsTest {

    private final static WebDriverType DRIVER_TYPE = WebDriverType.CHROME;

    private WebDriver webDriver;

    private BrowserMobProxy proxy;

    public void setUp() throws Exception {
        // create proxy instance
        proxy = ProxyFactory.getInstance();

        // configure it as a desired capability
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, ClientUtil.createSeleniumProxy(proxy));

        // initialize the web driver
        webDriver = WebDriverFactory.getInstance(DRIVER_TYPE, capabilities);
    }

    public void tearDown() throws Exception {
        proxy.stop();
        webDriver.quit();
    }

    @Test
    public void resourceLoadTimes() throws Exception {
        HashMap<String, Resource> resourceMap = new HashMap<>();

        System.out.println(
                "Will load " + TestSettings.URL_METRICS + " resources " + TestSettings.LOAD_CYCLES + " times...");

        for (int i = 0; i < TestSettings.LOAD_CYCLES; i++) {
            System.out.println("Loading cycle " + (i + 1) + "...");
            setUp();

            // create a new HAR for the desired URL
            proxy.newHar(TestSettings.URL_METRICS);

            webDriver.get(TestSettings.URL_METRICS);

            // get the HAR data
            Har har = proxy.getHar();

            har.getLog().getEntries().stream().filter(entry -> entry.getTime() > 0).forEach(entry -> {
                String url = entry.getRequest().getUrl();
                Resource resource = null;
                if (!resourceMap.containsKey(url)) {
                    resource = new Resource();
                    resource.setUrl(url);
                    resource.setType(entry.getResponse().getContent().getMimeType());
                    resourceMap.put(url, resource);
                } else {
                    resource = resourceMap.get(url);
                }
                resource.getTimes().add(new Long(entry.getTime(TimeUnit.MILLISECONDS)));
            });

            proxy.endHar();

            tearDown();
        }

        System.out.println("Will write JSON file: " + TestSettings.OUTPUT_JSON_FILE);
        writeJsonFile(resourceMap);

        System.out.println("Will write CSV file: " + TestSettings.OUTPUT_CSV_FILE);
        writeCsvFile(resourceMap);

        System.out.println("The following resources were recorded successfuly:");
        resourceMap.values().forEach(r -> System.out.println(r + ", average: " + r.getAverageTime()));

    }

    private void writeJsonFile(HashMap<String, Resource> resourceMap) throws Exception {
        Result result = new Result(TestSettings.URL_METRICS, TestSettings.LOAD_CYCLES, resourceMap.values());
        new ObjectMapper().writer().withDefaultPrettyPrinter().writeValue(new File(TestSettings.OUTPUT_JSON_FILE),
                result);
    }

    private void writeCsvFile(HashMap<String, Resource> resourceMap) throws Exception {
        CSVPrinter printer = CSVFormat.EXCEL.withHeader("url", "type", "time (millis)")
                .print(new FileWriter(TestSettings.OUTPUT_CSV_FILE));
        for (Resource resource : resourceMap.values()) {
            printer.print(resource.getUrl());
            printer.print(resource.getType());
            printer.print(resource.getAverageTime());
            printer.println();
        }
        printer.flush();
        printer.close();
    }

}
