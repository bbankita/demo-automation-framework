package com.framework.frontend.utilities;

import com.framework.exceptions.FrameworkException;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

import static com.framework.common.utilities.ConfigurationReader.getBrowserName;
import static com.framework.common.utilities.ConfigurationReader.isHeadless;

/**
 * The browser of one scenario. PicoContainer creates one instance per scenario and injects the same instance into
 * {@code BrowserSteps} and every page object, so parallel scenarios never share a browser and no ThreadLocal is needed.
 */
public class BrowserSession {

    private DriverManager driverManager;

    public void start() {
        if (driverManager == null) {
            driverManager = DriverManagerFactory.create(getBrowserName());
            driverManager.getDriver(isHeadless());
        }
    }

    public WebDriver getDriver() {
        if (driverManager == null) {
            throw new FrameworkException("No browser for this scenario. UI scenarios must be tagged @ui so the browser hook runs.");
        }
        return driverManager.getDriver();
    }

    public void attachScreenshot(Scenario scenario) {
        if (driverManager != null) {
            driverManager.createScreenshot(scenario);
        }
    }

    public void quit() {
        if (driverManager != null) {
            driverManager.quitDriver();
            driverManager = null;
        }
    }
}
