package com.framework.backend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@ToString
public class Article {

    private String title;
    private String description;
    private String body;
    private List<String> tagList;

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder(){
        return new Builder()
                .title(title)
                .description(description)
                .body(body)
                .tagList(tagList);
    }

    public static final class Builder{
        private final Article article = new Article();

        public Builder title(String title){
            article.title= title;
            return this;
        }

        public Builder description(String description){
            article.description= description;
            return this;
        }

        public Builder body(String body){
            article.body= body;
            return this;
        }

        public Builder tagList(List<String> tagList){
            article.tagList= tagList;
            return this;
        }

        public Article build(){
            return article;
        }
    }
}
