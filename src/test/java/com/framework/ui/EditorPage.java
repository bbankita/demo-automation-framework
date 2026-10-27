package com.framework.ui;

import com.framework.core.logging.LogHelper;
import com.framework.core.driver.BrowserSession;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static com.framework.core.config.ConfigurationReader.getConfigurations;

public class EditorPage extends BasePage {

    private static final By publishButton = By.xpath("//button[text()='Publish Article']");
    private static final By articleTitleField = By.cssSelector("input[placeholder*= 'Title']");
    private static final By articleDescriptionField = By.cssSelector("input[name= 'description']");
    private static final By articleBodyField = By.cssSelector("textarea[name= 'body']");
    private static final By articleTagsField = By.cssSelector("input[name= 'tags']");
    private static final By updateButton = By.xpath("//button[text()='Update Article']");

    private static final String PAGE_URL = "/#/editor";

    public EditorPage(BrowserSession browserSession) {
        super(browserSession);
    }

    public void open() {
        LogHelper.info("[Browser] Opening " + getConfigurations().getWebUrl()+ PAGE_URL);
        webDriver.navigate().to(getConfigurations().getWebUrl() + PAGE_URL);
        waitForVisible(publishButton, "publish button");
    }

    public void clickPublishButton() {
        click(publishButton, "publish button");
    }

    public void enterArticleTitle(String title) {
        type(articleTitleField, title, "article title field");
    }

    public void enterArticleDescription(String description) {
        type(articleDescriptionField, description, "article description field");
    }

    public void enterArticleBody(String body) {
        type(articleBodyField, body, "article body field");
    }

    public void enterArticleTags(List<String> tags) {
        Actions actions = new Actions(webDriver);
        WebElement element = waitForClickable(articleTagsField, "article tags field");
        actions.click(element);
        for(int i=0; i<tags.size(); i++) {
            actions.sendKeys(tags.get(i));
            if(i<tags.size()-1) {
                actions.sendKeys(Keys.SPACE);
            }
        }
        actions.perform();
    }

    public void clickUpdateArticleButton() {
        click(updateButton, "update button");
    }
}
