package com.framework.steps.api;

import com.framework.api.ArticleApi;
import com.framework.context.ArticleContext;
import com.framework.model.Article;
import com.framework.model.ArticleResponse;
import com.framework.utilities.TestDataFactory;
import io.cucumber.java.en.Given;

public class ArticleSteps {

    ArticleContext articleContext;

    private final ArticleApi articleApi;

    public ArticleSteps(ArticleContext articleContext, ArticleApi articleApi) {
        this.articleContext = articleContext;
        this.articleApi = articleApi;
    }

    @Given("I have published an article")
    public void iHavePublishedAnArticle() {
        Article article = TestDataFactory.createArticle();
        ArticleResponse articleResponse = articleApi.createArticle(article);
        articleContext.setCurrentArticleResponse(articleResponse);
        articleContext.setCurrentArticle(article);
        articleContext.addCreatedSlug(articleResponse.getSlug());
        articleContext.setCurrentArticleSlug(articleResponse.getSlug());
    }

}
