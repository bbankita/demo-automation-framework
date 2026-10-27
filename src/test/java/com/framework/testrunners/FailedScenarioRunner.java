package com.framework.testrunners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Reruns only the scenarios listed by TestRunner's rerun plugin. Writes a second JSON file into
 * the same directory so Cluecumber shows both runs in one report.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "@target/failed-scenarios.txt",
        glue = {
                "com.framework.steps",
        },
        plugin = {
                "summary",
                "json:target/cucumber-report/rerun.json"
        },
        monochrome = true
)
public class FailedScenarioRunner {
}
