package com.framework.api;

import com.framework.model.Article;
import com.framework.model.ArticleResponse;
import com.framework.model.User;
import com.framework.core.http.HttpRestClient;
import com.framework.core.logging.LogHelper;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

public class ArticleApi extends BaseApi {

    private static final String ARTICLES = "/articles";
    private static final String ARTICLES_SLUG = "/articles/{slug}";

    public ArticleApi(HttpRestClient httpRestClient) {
        super(httpRestClient);
    }

    public ArticleResponse createArticle(Article article) {
        LogHelper.info("Creating article with title " + article.getTitle());
        Response response = httpRestClient.request(Method.POST, ARTICLES).body(Map.of("article", article)).send();
        requireStatus(response,"Create article with title: "+article.getTitle(), 201);
        return response.jsonPath().getObject("article", ArticleResponse.class);
    }

    public List<ArticleResponse> getArticleByAuthor(User user) {
        LogHelper.info("Fetch article with user " + user.getEmail());
        Response response = httpRestClient.request(Method.GET, ARTICLES).queryParam("author", user.getUsername()).send();
        requireStatus(response,"Get articles of user: "+user.getUsername(), 200);
        return response.jsonPath().getList("articles", ArticleResponse.class);
    }

    public void deleteArticle(String slug) {
        LogHelper.info("Delete article with slug " + slug);
        Response response = httpRestClient.request(Method.DELETE, ARTICLES_SLUG).pathParam("slug",slug).send();
        requireStatus(response,"Delete articles with slug: "+slug, 200);
    }

}
