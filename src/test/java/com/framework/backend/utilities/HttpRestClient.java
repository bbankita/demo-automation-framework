package com.framework.backend.utilities;

import com.framework.backend.context.CommonContext;
import com.framework.common.utilities.LogHelper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static com.framework.common.utilities.ConfigurationReader.getConfigurations;

/**
 * The single path for every API call: {@code request(method, path)} returns an {@link ApiRequest} to describe
 * the call fluently, and nothing is sent until {@link ApiRequest#send()}. Each send starts from a fresh request
 * built on an immutable base spec, so headers or bodies from one request can never leak into the next.
 */
public class HttpRestClient {

    private final CommonContext commonContext;
    private final RequestSpecification baseSpec;
    @Setter
    @Getter
    private String jwtToken;
    @Setter
    @Getter
    private String rawAuthorizationHeader;

    public HttpRestClient(CommonContext commonContext) {
        this.commonContext = commonContext;
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(getConfigurations().getApiBaseUri())
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                // the jwt token travels in the Authorization header; keep it out of logs and reports
                .setConfig(RestAssuredConfig.config().logConfig(LogConfig.logConfig().blacklistHeader("Authorization")))
                .build();
    }

    public ApiRequest request(Method method, String path) {
        return new ApiRequest(this, method, path);
    }

    Response send(ApiRequest apiRequest) {
        LogHelper.logSendHttpRequest(apiRequest.getMethod(), getConfigurations().getApiBaseUri() + apiRequest.getPath());
        ByteArrayOutputStream exchangeLog = new ByteArrayOutputStream();
        PrintStream logStream = new PrintStream(exchangeLog, true);

        RequestSpecification request = RestAssured.given()
                .spec(baseSpec)
                .filter(new RequestLoggingFilter(apiRequest.isSensitive() ? LogDetail.URI : LogDetail.ALL, true, logStream, true))
                .filter(new ResponseLoggingFilter(apiRequest.isSensitive() ? LogDetail.STATUS : LogDetail.ALL, true, logStream));
        if (apiRequest.getBody() != null) {
            request.body(apiRequest.getBody());
        }
        if (apiRequest.getQueryParams() != null) {
            request.queryParams(apiRequest.getQueryParams());
        }
        if (apiRequest.getPathParams() != null) {
            request.pathParams(apiRequest.getPathParams());
        }
        if(apiRequest.getExtraHeaders()!= null) {
            for (Map.Entry<String, String> entry : apiRequest.getExtraHeaders().entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        if (rawAuthorizationHeader != null) {
            request.header("Authorization", rawAuthorizationHeader);
        } else if (jwtToken != null) {
            request.header("Authorization", "Token " + jwtToken);
        }

        Response response = request.request(apiRequest.getMethod(), apiRequest.getPath());
        commonContext.storeResponse(response);
        commonContext.recordExchange(exchangeLog.toString());
        return response;
    }

    public void clearAuthToken() {
        this.jwtToken = null;
        this.rawAuthorizationHeader = null;
    }

}
