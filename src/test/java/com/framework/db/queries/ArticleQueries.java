package com.framework.db.queries;

import com.framework.core.db.DatabaseClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ArticleQueries {

    DatabaseClient databaseClient;

    public ArticleQueries(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Optional<String> getSlugByTitle(String title){
        String query = "SELECT slug FROM \"Articles\" WHERE title = ?";
        List<Map<String, Object>> rows = databaseClient.executeQuery(query,title);
        if(rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable((String) rows.get(0).get("slug"));
    }

}
