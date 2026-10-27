package com.framework.context;

import io.restassured.response.Response;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Per-scenario store for the latest API response and a log of every HTTP exchange,
 * which is attached to the report when a scenario fails.
 */
public class CommonContext {

    @Getter
    private Response response;
    private final List<String> exchanges = new ArrayList<>();

    public void storeResponse(Response response) {
        this.response = response;
    }

    public int getResponseStatusCode() {
        return response.getStatusCode();
    }

    public void recordExchange(String exchange) {
        exchanges.add(exchange);
    }

    public List<String> getExchanges() {
        return Collections.unmodifiableList(exchanges);
    }

    public  <T> T getResponseAs(String rootPath, Class<T> type) {
        return response.jsonPath().getObject(rootPath, type);
    }
}
