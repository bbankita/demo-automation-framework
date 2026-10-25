package com.framework.backend.context;

import com.framework.backend.model.Article;
import com.framework.backend.model.ArticleResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ArticleContext {

    @Setter
    private Article currentArticle;
    @Setter
    private ArticleResponse currentArticleResponse;
    @Setter
    private List<ArticleResponse> currentArticleResponses;
    @Setter
    private String currentArticleSlug;
    @Getter
    private List<String> createdSlugs = new ArrayList<>();

    public ArticleContext() {
    }

    public Article getCurrentArticle() {
        if (currentArticle == null) {
            throw new IllegalStateException("No Article has been added in this scenario yet");
        }
        return currentArticle;
    }

    public String getCurrentArticleSlug() {
        if (currentArticleSlug == null) {
            throw new IllegalStateException("No Article Slug has been added in this scenario yet");
        }
        return currentArticleSlug;
    }

    public ArticleResponse getCurrentArticleResponse() {
        if (currentArticleResponse == null) {
            throw new IllegalStateException("No ArticleResponse has been added in this scenario yet");
        }
        return currentArticleResponse;
    }

    public List<ArticleResponse> getCurrentArticleResponses(){
        if(currentArticleResponses==null)
            throw new IllegalStateException("No ArticleResponses have been added in this scenario yet");
        return currentArticleResponses;
    }

    public void addCreatedSlug(String slug){
        createdSlugs.add(slug);
    }

    public void updatedTrackedSlug(String oldSlug, String updatedSlug){
        if(!createdSlugs.contains(oldSlug))
            throw new IllegalStateException("Slug not tracked :"+oldSlug);
        createdSlugs.set(createdSlugs.indexOf(oldSlug), updatedSlug);
    }
}


