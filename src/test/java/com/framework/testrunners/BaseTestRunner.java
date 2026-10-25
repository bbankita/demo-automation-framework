package com.framework.testrunners;

import com.framework.common.utilities.LogHelper;
import com.framework.common.utilities.TestDataFactory;
import io.cucumber.junit.CucumberOptions;
import org.junit.BeforeClass;

import static com.framework.common.utilities.ConfigurationReader.getBrowserName;
import static com.framework.common.utilities.ConfigurationReader.getConfigurations;
import static com.framework.common.utilities.ConfigurationReader.getEnvironment;
import static com.framework.common.utilities.ConfigurationReader.isHeadless;

/**
 * Shared Cucumber options. Subclasses add their own plugins.
 */
@CucumberOptions(
        features = "classpath:features",
        glue = {
                "com.framework.backend.stepdefinitions",
                "com.framework.frontend.stepdefinitions",
                "com.framework.common.stepdefinitions",
                "com.framework.common.utilities"
        },
        plugin = {"summary"},
        monochrome = true
)
public abstract class BaseTestRunner {

    @BeforeClass
    public static void logRunConfiguration() {
        LogHelper.info("[Run] tag=" + TestDataFactory.RUN_TAG
                + " env=" + getEnvironment()
                + " api=" + getConfigurations().getApiBaseUri()
                + " web=" + getConfigurations().getWebUrl()
                + " browser=" + getBrowserName()
                + " headless=" + isHeadless());
    }
}
