package com.framework.frontend.utilities;

import com.framework.common.utilities.LogHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

public class ChromeDriverManager extends DriverManager {

    @Override
    protected WebDriver createDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=" + WINDOW_SIZE.getWidth() + "," + WINDOW_SIZE.getHeight());
        // Stop Chrome's password manager / breach-check popups from interfering with login flows
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        WebDriver driver = new ChromeDriver(options);
        LogHelper.info("[DriverSetUp] Chrome started (headless=" + headless + ")");
        return driver;
    }
}
