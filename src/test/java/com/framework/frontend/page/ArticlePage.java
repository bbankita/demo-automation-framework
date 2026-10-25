package com.framework.frontend.page;

import com.framework.common.utilities.LogHelper;
import com.framework.frontend.utilities.BrowserSession;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.framework.common.utilities.ConfigurationReader.getConfigurations;

public class ArticlePage extends BasePage {

    private static final String ARTICLE_PAGE_URL = "/#/article/%s";

    private static final By articleTitle = By.cssSelector("div.banner h1");
    // the rendered markdown is the first child; the tag list is its sibling, so the whole div would include tag text
    //private static final By articleBody = By.cssSelector("div.article-content > div > :first-child");
    private static final By articleBody = By.xpath("//div[contains(@class,'article-content')]/descendant::p");
    private static final By articleTags = By.cssSelector("ul.tag-list li");
    private static final By articleAuthorBanner = By.xpath("//div[@class='banner']/descendant::a[@class='author']");
    private static final By articleBodyBoldTexts = By.cssSelector("div.article-content strong");
    private static final By articleBodyItalicTexts = By.cssSelector("div.article-content em");
    private static final By articleEditButton = By.xpath("//div[@class='banner']/descendant::a[contains(text(),'Edit Article')]");

    public ArticlePage(BrowserSession browserSession) {
        super(browserSession);
    }


    public void open(String articleSlug) {
        LogHelper.info("[Browser] Opening " + getConfigurations().getWebUrl() + ARTICLE_PAGE_URL);
        webDriver.navigate().to(getConfigurations().getWebUrl() + String.format(ARTICLE_PAGE_URL, articleSlug));
    }

    public String getArticleTitle() {
        return getText(articleTitle, "article title");
    }

    public String getArticleAuthorInBanner() {
        return getText(articleAuthorBanner, "article author");
    }

    public List<String> getArticleBodyPragraphs() {
        return textsOf(waitForAllVisible(articleBody, "article body"));
    }

    // A body may legitimately have no bold/italic text, so wait for the body to render and then allow an empty list
    public List<String> getBoldTextsInBody() {
        waitForVisible(articleBody, "article body");
        return textsOf(webDriver.findElements(articleBodyBoldTexts));
    }

    public List<String> getItalicTextsInBody() {
        waitForVisible(articleBody, "article body");
        return textsOf(webDriver.findElements(articleBodyItalicTexts));
    }

    public List<String> getArticleTags() {
        return textsOf(waitForAllVisible(articleTags, "article tags"));
    }

    public void clickArticleEditButton() {
        click(articleEditButton, "edit button");
    }

    private static List<String> textsOf(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public void waitForArticleUrl(String articleSlug) {
        String articlePath = String.format(ARTICLE_PAGE_URL, articleSlug);
        // anchored so a slug that is only a prefix of the current one does not match
        waitUntil(ExpectedConditions.urlMatches(Pattern.quote(articlePath) + "$"), "URL to end with " + articlePath);
    }

    public void reload() {
        LogHelper.info("[Browser] Reloading " + webDriver.getCurrentUrl());
        webDriver.navigate().refresh();
    }

    public void waitForTitle(String title) {
        waitUntil(ExpectedConditions.textToBe(articleTitle, title), "article title to be " + title);
    }
}
