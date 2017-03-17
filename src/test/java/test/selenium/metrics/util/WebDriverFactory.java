package test.selenium.metrics.util;

import java.lang.reflect.Constructor;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverFactory {

    public static WebDriver getInstance(WebDriverType webDriverType) throws Exception {
        return getInstance(webDriverType, null);
    }

    public static WebDriver getInstance(WebDriverType webDriverType, Capabilities capabilities) throws Exception {
        System.setProperty(webDriverType.getSystemProperty(), webDriverType.getPathToDriver());
        return initializeDriver(webDriverType, capabilities);
    }

    private static WebDriver initializeDriver(WebDriverType webDriverType, Capabilities capabilities) throws Exception {
        Class<? extends WebDriver> clazz = null;
        switch (webDriverType) {
        case FIREFOX:
            clazz = FirefoxDriver.class;
            break;
        default:
            clazz = ChromeDriver.class;
            break;
        }

        Constructor<? extends WebDriver> constructor = null;
        if (capabilities != null) {
            constructor = clazz.getConstructor(Capabilities.class);
            return constructor.newInstance(capabilities);
        } else {
            return clazz.newInstance();
        }
    }

}
