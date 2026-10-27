package com.framework.steps.hooks;

import com.framework.api.ArticleApi;
import com.framework.context.ArticleContext;
import com.framework.context.CommonContext;
import com.framework.core.logging.LogHelper;
import com.framework.core.exceptions.FrameworkException;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

import java.util.List;
import java.util.regex.Pattern;

public class SharedHooks {

    private static final Pattern TOKEN_VALUE = Pattern.compile("(\"token\"\\s*:\\s*)\"[^\"]*\"");

    private final CommonContext commonContext;
    ArticleContext articleContext;
    ArticleApi articleApi;

    public SharedHooks(CommonContext commonContext, ArticleContext articleContext, ArticleApi articleApi) {
        this.commonContext = commonContext;
        this.articleContext = articleContext;
        this.articleApi = articleApi;
    }

    @After(value= "@articles", order = 10000)
    public void cleanUpTestArticles() {
        List<String> articleSlugs = articleContext.getCreatedSlugs();
        for(String articleSlug: articleSlugs){
            try{
                articleApi.deleteArticle(articleSlug);
                LogHelper.cleanupInfoLog("Deleted article with slug: "+articleSlug);
            } catch (FrameworkException ex){
                LogHelper.warn("[CLEANUP] Failed. Article slug: "+articleSlug);
            }
        }

    }
    /** Runs before cleanup */
    @After(order = 20000)
    public void attachHttpExchangesOnFailure(Scenario scenario) {
        if (scenario.isFailed() && !commonContext.getExchanges().isEmpty()) {
            scenario.attach(String.join("\n----------------\n", commonContext.getExchanges()),
                    "text/plain", "HTTP exchanges");
        }
    }

    @After( order = 20000)
    public void attachResponseBodyOnFailure(Scenario scenario) {
        Response response = commonContext.getResponse();
        if (scenario.isFailed() && response != null) {
            String body = TOKEN_VALUE.matcher(response.asPrettyString()).replaceAll("$1\"***\"");
            String mediaType = response.getContentType() != null && response.getContentType().contains("json")
                    ? "application/json" : "text/plain";
            scenario.attach(body, mediaType, "Last response body (" + response.getStatusCode() + ")");
        }
    }
}
