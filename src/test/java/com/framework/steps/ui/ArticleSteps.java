package com.framework.steps.ui;

import com.framework.context.ArticleContext;
import com.framework.model.Article;
import com.framework.db.queries.ArticleQueries;
import com.framework.utilities.MarkdownParser;
import com.framework.utilities.TestDataFactory;
import com.framework.core.exceptions.FrameworkException;
import com.framework.ui.ArticlePage;
import com.framework.ui.EditorPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.SoftAssertions;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.framework.core.config.ConfigurationReader.getConfigurations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class ArticleSteps {

    private final ArticleContext articleContext;
    private final EditorPage editorPage;
    private final ArticlePage articlePage;
    private final ArticleQueries articleQueries;

    public ArticleSteps(ArticleContext articleContext, EditorPage editorPage, ArticlePage articlePage, ArticleQueries articleQueries) {
        this.articleContext = articleContext;
        this.editorPage = editorPage;
        this.articlePage = articlePage;
        this.articleQueries = articleQueries;
    }

    @When("I publish an article with:")
    public void iPublishAnArticleWith(Article article) {
        articleContext.setCurrentArticle(article);
        editorPage.enterArticleTitle(article.getTitle());
        editorPage.enterArticleDescription(article.getDescription());
        editorPage.enterArticleBody(article.getBody());
        editorPage.enterArticleTags(article.getTagList());
        editorPage.clickPublishButton();
        articlePage.waitForTitle(article.getTitle());
        String slug = articleQueries.getSlugByTitle(articleContext.getCurrentArticle().getTitle()).orElseThrow(() -> new FrameworkException("No article retrieved with title "+article.getTitle()));
        articleContext.addCreatedSlug(slug);
        articleContext.setCurrentArticleSlug(slug);
    }

    @And("I am on the Editor page")
    public void iAmOnTheEditorPage() {
        editorPage.open();
    }

    @Then("the article page should show the title, author as {string} and rendered body")
    public void theArticlePageShouldShowTheTitleAuthorAndRenderedBody(String userKey) {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(articlePage.getArticleTitle()).as("Article title").isEqualTo(articleContext.getCurrentArticle().getTitle());
        softly.assertThat(articlePage.getArticleAuthorInBanner()).as("Article author").isEqualTo(getConfigurations().getUser(userKey).getUsername());
        String body = articleContext.getCurrentArticle().getBody();
        softly.assertThat(articlePage.getArticleBodyPragraphs()).as("Rendered body text").isEqualTo(MarkdownParser.renderedParagraphs(body));
        softly.assertThat(articlePage.getBoldTextsInBody()).as("Bold text rendered from markdown").containsExactlyElementsOf(MarkdownParser.boldTexts(body));
        softly.assertThat(articlePage.getItalicTextsInBody()).as("Italic text rendered from markdown").containsExactlyElementsOf(MarkdownParser.italicTexts(body));
        softly.assertAll();
    }

    @And("the article should display the tags:")
    public void theArticleShouldDisplayTheTags(List<String> expectedTags) {
        SoftAssertions softly = new SoftAssertions();
        List<String> actualTags = articlePage.getArticleTags();
        softly.assertThat(actualTags).as("Article tags").containsExactlyInAnyOrderElementsOf(expectedTags);
        softly.assertAll();
    }

    @When("I change the article title to a new unique title")
    public void iChangeTheArticleTitleToANewUniqueTitle() {
        articlePage.clickArticleEditButton();
        Article article = articleContext.getCurrentArticle();
        article= article.toBuilder().title(TestDataFactory.uniqueArticleTitle()).build();
        articleContext.setCurrentArticle(article);
        editorPage.enterArticleTitle(article.getTitle());
        editorPage.clickUpdateArticleButton();
        String oldSlug = articleContext.getCurrentArticleSlug();
        String title = article.getTitle();
        String newSlug = await("slug in DB for title " + title)
                .atMost(Duration.ofSeconds(getConfigurations().getTimeoutSeconds()))
                .pollInterval(Duration.ofMillis(500))
                .until(() -> articleQueries.getSlugByTitle(title), Optional::isPresent)
                .get();
        articleContext.updatedTrackedSlug(oldSlug, newSlug);
        articleContext.setCurrentArticleSlug(newSlug);
    }

    @And("I am on the Article Page")
    public void iAmOnTheArticlePage() {
        articlePage.open(articleContext.getCurrentArticleResponse().getSlug());
    }

    // Lower-cases the title and collapses each run of non-alphanumeric characters into a single hyphen
    private static String toSlug(String title) {
        return title.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    @Then("the article page should show the new title")
    public void theArticlePageShouldShowTheNewTitle() {
        articlePage.waitForTitle(articleContext.getCurrentArticle().getTitle());
        assertThat(articlePage.getArticleTitle()).as("Article title").isEqualTo(articleContext.getCurrentArticle().getTitle());
    }

    @And("the article should be reachable at its updated URL")
    public void theArticleShouldBeReachableAtItsUpdatedUrl() {
        String updatedTitle = articleContext.getCurrentArticle().getTitle();
        String slug= articleContext.getCurrentArticleSlug();
        articlePage.waitForArticleUrl(slug);
        articlePage.reload();
        assertThat(articlePage.getArticleTitle()).as("Article title after reloading updated URL").isEqualTo(updatedTitle);
    }
}
