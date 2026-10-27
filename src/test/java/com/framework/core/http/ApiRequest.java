package com.framework.core.http;

import io.restassured.http.Method;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter(AccessLevel.PACKAGE)
public class ApiRequest {

    private final HttpRestClient httpRestClient;
    private final Method method;
    private final String path;
    private Object body;
    private final Map<String, Object> queryParams = new HashMap<>();
    private final Map<String, Object> pathParams = new HashMap<>();
    private final Map<String, String> extraHeaders = new HashMap<>();
    private boolean sensitive;

    ApiRequest(HttpRestClient httpRestClient,Method method, String path) {
        this.method = method;
        this.httpRestClient = httpRestClient;
        this.path = path;
    }

    public ApiRequest body(Object body) {
        this.body = body;
        return this;
    }

    public ApiRequest queryParam(String name, Object value){
        queryParams.put(name, value);
        return this;
    }

    public ApiRequest pathParam(String name, Object value){
        pathParams.put(name, value);
        return this;
    }

    public ApiRequest header(String name, String value){
        extraHeaders.put(name, value);
        return this;
    }

    public ApiRequest sensitive(){
        sensitive=true;
        return this;
    }

    public Response send() {
        return httpRestClient.send(this); // Client builds and sends using its baseSpec/context
    }

}
