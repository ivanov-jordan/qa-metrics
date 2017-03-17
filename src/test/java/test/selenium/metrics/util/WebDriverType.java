package test.selenium.metrics.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum WebDriverType {

    FIREFOX("webdriver.gecko.driver", "c:\\Webdrivers\\geckodriver\\geckodriver.exe"), 
    CHROME("webdriver.chrome.driver", "c:\\Webdrivers\\chrome\\chromedriver.exe");

    private String systemProperty;
    private String pathToDriver;

}
