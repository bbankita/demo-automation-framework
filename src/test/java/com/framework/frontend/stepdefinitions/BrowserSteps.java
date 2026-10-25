package com.framework.frontend.stepdefinitions;

import com.framework.common.utilities.LogHelper;
import com.framework.frontend.utilities.BrowserSession;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class BrowserSteps {

    private final BrowserSession browserSession;

    public BrowserSteps(BrowserSession browserSession) {
        this.browserSession = browserSession;
    }

    @Before("@ui")
    public void setupBrowser() {
        browserSession.start();
    }

    @After("@ui")
    public void afterScenarioBrowserCleanup(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                browserSession.attachScreenshot(scenario);
            }
        } finally {
            LogHelper.info("[BrowserCleanUp] Closing browser");
            browserSession.quit();
        }
    }
}
