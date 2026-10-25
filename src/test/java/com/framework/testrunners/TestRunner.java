package com.framework.testrunners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "json:target/cucumber-report/cucumber.json",
                "html:target/cucumber-html/index.html",
                "rerun:target/failed-scenarios.txt"
        }
)
public class TestRunner extends BaseTestRunner {
}
