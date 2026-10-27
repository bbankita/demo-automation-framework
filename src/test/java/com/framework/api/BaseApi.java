package com.framework.api;

import com.framework.core.http.HttpRestClient;
import com.framework.core.exceptions.FrameworkException;
import io.restassured.response.Response;

import java.util.Arrays;

public abstract class BaseApi {

    protected final HttpRestClient httpRestClient;

    protected BaseApi(HttpRestClient httpRestClient) {
        this.httpRestClient = httpRestClient;
    }

    protected Response requireStatus(Response response, String action, int... expectedCodes){
        int statusCode = response.getStatusCode();
        if (Arrays.stream(expectedCodes).noneMatch(code -> code == statusCode)) {
            throw new FrameworkException("Could not perform action: " + action + "\nStatus code: "
                    + response.getStatusCode() +" Expected status one of "+ Arrays.toString(expectedCodes) + "\nResponse:" + response.asString());
        }
        return response;
    }
}
