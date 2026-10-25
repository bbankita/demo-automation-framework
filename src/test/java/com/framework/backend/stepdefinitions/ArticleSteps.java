package com.framework.backend.stepdefinitions;

import com.framework.backend.api.ArticleApi;
import com.framework.backend.context.ArticleContext;
import com.framework.backend.model.Article;
import com.framework.backend.model.ArticleResponse;
import com.framework.common.utilities.TestDataFactory;
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
