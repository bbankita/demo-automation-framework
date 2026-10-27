package com.framework.steps.transformers;

import com.framework.model.Article;
import com.framework.model.User;
import com.framework.utilities.TestDataFactory;
import io.cucumber.java.DataTableType;

import java.util.List;
import java.util.Map;

public class DataTableTransformers {

    @DataTableType
    public User userRegistrationEntryTransformer(Map<String, String> row) {
        return User.builder()
                .email(row.get("email"))
                .password(row.get("password"))
                .username(row.get("username")).build();
    }

    @DataTableType
    public Article articleEntryTransformer(Map<String, String> row) {
        return Article.builder()
                .title("<unique>".equals(row.get("title")) ? TestDataFactory.uniqueArticleTitle() : row.get("title"))
                .description(row.get("description"))
                .body(row.get("body"))
                .tagList(List.of(row.get("tags").split(","))).build();
    }

}
