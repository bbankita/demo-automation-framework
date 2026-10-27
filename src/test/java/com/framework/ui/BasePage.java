package com.framework.ui;

import com.framework.core.exceptions.FrameworkException;
import com.framework.core.driver.BrowserSession;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.framework.core.config.ConfigurationReader.getConfigurations;

/**
 * Base for page objects. All interaction goes through explicit waits with one configurable
 * timeout, and a timeout fails with a message that says what was being waited for.
 */
public abstract class BasePage {

    protected final WebDriver webDriver;
    private final WebDriverWait wait;
    private final int timeoutSeconds;

    protected BasePage(BrowserSession browserSession) {
        webDriver = browserSession.getDriver();
        timeoutSeconds = getConfigurations().getTimeoutSeconds();
        wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeoutSeconds));
    }

    protected <T> T waitUntil(ExpectedCondition<T> condition, String description) {
        try {
            return wait.until(condition);
        } catch (TimeoutException e) {
            throw new FrameworkException("Timed out after " + timeoutSeconds + "s waiting for " + description
                    + " on " + webDriver.getCurrentUrl(), e);
        }
    }

    // Helpers take a By and look the element up on every poll, so an element that renders late is still found.
    // Never pass webDriver.findElement(...) into a wait: it throws before the wait starts.

    protected WebElement waitForVisible(By locator, String description) {
        return waitUntil(ExpectedConditions.visibilityOfElementLocated(locator), description + " to be visible");
    }

    protected List<WebElement> waitForAllVisible(By locator, String description) {
        return waitUntil(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator), description + " to be visible");
    }

    protected WebElement waitForClickable(By locator, String description) {
        return waitUntil(ExpectedConditions.elementToBeClickable(locator), description + " to be clickable");
    }

    protected void click(By locator, String description) {
        waitForClickable(locator, description).click();
    }

    protected void type(By locator, String text, String description) {
        WebElement field = waitForVisible(locator, description);
        field.clear();
        field.sendKeys(text);
    }

    protected String getText(By locator, String description) {
        return waitForVisible(locator, description).getText();
    }

    protected void pressKey(By locator, Keys key, String description) {
        waitForVisible(locator, description).sendKeys(key);
    }

}
