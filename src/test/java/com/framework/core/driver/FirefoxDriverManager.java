package com.framework.core.driver;

import com.framework.core.logging.LogHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxDriverManager extends DriverManager {

    @Override
    protected WebDriver createDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=" + WINDOW_SIZE.getWidth(), "--height=" + WINDOW_SIZE.getHeight());
        WebDriver driver = new FirefoxDriver(options);
        LogHelper.info("[DriverSetUp] Firefox started (headless=" + headless + ")");
        return driver;
    }
}
