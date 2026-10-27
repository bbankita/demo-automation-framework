package com.framework.core.driver;

import io.cucumber.java.Scenario;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Template for a browser's lifecycle. Subclasses only describe how to build their driver;
 * Selenium Manager resolves the matching driver binary automatically.
 */
public abstract class DriverManager {

    protected static final Dimension WINDOW_SIZE = new Dimension(1920, 1080);

    // Selenium logs through java.util.logging; its CDP version warnings are noise because the
    // framework does not use DevTools. Held in a field so the level is not garbage collected.
    private static final Logger SELENIUM_LOGGER = Logger.getLogger("org.openqa.selenium");

    static {
        SELENIUM_LOGGER.setLevel(Level.SEVERE);
    }

    // On a cold machine Selenium Manager downloads the browser/driver on first use; parallel
    // threads doing that at once collide on its cache. Serialize only until the first start succeeds.
    private static final Object FIRST_START_LOCK = new Object();
    private static volatile boolean binariesResolved;

    private WebDriver driver;

    protected abstract WebDriver createDriver(boolean headless);

    public WebDriver getDriver(boolean headless) {
        if (driver == null) {
            if (binariesResolved) {
                driver = createDriver(headless);
            } else {
                synchronized (FIRST_START_LOCK) {
                    driver = createDriver(headless);
                    binariesResolved = true;
                }
            }
            // maximize() is a no-op in headless mode, so always set an explicit size
            driver.manage().window().setSize(WINDOW_SIZE);
        }
        return driver;
    }

    public WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException("Driver has not been started for this scenario");
        }
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public void createScreenshot(Scenario scenario) {
        if (driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName() + " - failure");
        }
    }
}
