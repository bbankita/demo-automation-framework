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
 * The single path for every API call. Each call starts from a fresh request built on an
 * immutable base spec, so headers or bodies from one request can never leak into the next.
 */
public class HttpRestClient {

    private final CommonContext commonContext;
    private final RequestSpecification baseSpec;
    @Setter @Getter
    private String jwtToken;
    @Setter @Getter
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

    public Response sendHttpRequest(Method method, String path) {
        return send(method, path, null, null,null,false);
    }

    public Response sendHttpRequest(Method method, String path, Object body) {
        return send(method, path, body, null,null,false);
    }

    public Response sendHttpRequestWithQueryParams(Method method, String path, Map<String, ?> queryParams) {
        return send(method, path, null, queryParams, null,false);
    }

    public Response sendHttpRequestWithPathParams(Method method, String path, Map<String, ?> pathParams) {
        return send(method, path, null, null, pathParams, false);
    }

    /** Same as {@link #sendHttpRequest} but bodies (e.g. credentials, tokens) are not logged. */
    public Response sendSensitiveHttpRequest(Method method, String path, Object body) {
        return send(method, path, body, null,null,true);
    }

    private Response send(Method method, String path, Object body, Map<String,?> queryParams, Map<String,?> pathParams, boolean sensitive) {
        LogHelper.logSendHttpRequest(method, getConfigurations().getApiBaseUri() + path);

        ByteArrayOutputStream exchangeLog = new ByteArrayOutputStream();
        PrintStream logStream = new PrintStream(exchangeLog, true);

        RequestSpecification request = RestAssured.given()
                .spec(baseSpec)
                .filter(new RequestLoggingFilter(sensitive ? LogDetail.URI : LogDetail.ALL, true, logStream, true))
                .filter(new ResponseLoggingFilter(sensitive ? LogDetail.STATUS : LogDetail.ALL, true, logStream));
        if (body != null) {
            request.body(body);
        }
        if(queryParams != null){
            request.queryParams(queryParams);
        }
        if(pathParams != null){
            request.pathParams(pathParams);
        }
        if(rawAuthorizationHeader != null) {
            request.header("Authorization", rawAuthorizationHeader);
        }
        else if (jwtToken != null) {
            request.header("Authorization", "Token " + jwtToken);
        }

        Response response = request.request(method, path);
        commonContext.storeResponse(response);
        commonContext.recordExchange(exchangeLog.toString());
        return response;
    }

    public void clearAuthToken() {
        this.jwtToken = null;
        this.rawAuthorizationHeader = null;
    }

}
