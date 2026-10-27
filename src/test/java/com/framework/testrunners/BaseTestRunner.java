package com.framework.testrunners;

import com.framework.core.logging.LogHelper;
import com.framework.utilities.TestDataFactory;
import io.cucumber.junit.CucumberOptions;
import org.junit.BeforeClass;

import static com.framework.core.config.ConfigurationReader.getBrowserName;
import static com.framework.core.config.ConfigurationReader.getConfigurations;
import static com.framework.core.config.ConfigurationReader.getEnvironment;
import static com.framework.core.config.ConfigurationReader.isHeadless;

/**
 * Shared Cucumber options. Subclasses add their own plugins.
 */
@CucumberOptions(
        features = "classpath:features",
        glue = {
                "com.framework.steps"
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
