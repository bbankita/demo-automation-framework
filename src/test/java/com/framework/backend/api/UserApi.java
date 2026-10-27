package com.framework.backend.api;

import com.framework.backend.model.User;
import com.framework.backend.model.UserResponse;
import com.framework.backend.utilities.HttpRestClient;
import com.framework.backend.utilities.JwtTokenFactory;
import com.framework.common.utilities.LogHelper;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

public class UserApi extends BaseApi {

    private static final String LOGIN = "/users/login";
    private static final String USERS = "/users";
    private static final String GET_CURRENT_USER_ENDPOINT = "/user";

    public UserApi(HttpRestClient httpRestClient) {
        super(httpRestClient);
    }

    public UserResponse login(User user) {
        Response response = loginRaw(user);
        requireStatus(response, String.format("Login as %s", user.getEmail()), 200);
        UserResponse loggedIn = response.jsonPath().getObject("user", UserResponse.class);
        httpRestClient.setJwtToken(loggedIn.getToken());
        return loggedIn;
    }

    public Response loginRaw(User user) {
        LogHelper.info("Logging in as " + user.getEmail());
        return httpRestClient.request(Method.POST, LOGIN).sensitive().body(Map.of("user", user)).send();
    }

    public UserResponse register(User user) {
        Response response = registerRaw(user);
        requireStatus(response, "Register user " + user.getEmail(),  201);
        UserResponse userResponse = response.jsonPath().getObject("user", UserResponse.class);
        httpRestClient.setJwtToken(userResponse.getToken());
        return userResponse;
    }

    public Response registerRaw(User user) {
        return httpRestClient.request(Method.POST, USERS).sensitive().body(Map.of("user", user)).send();
    }

    public void ensureRegisteredUser(User user) {
        Response response = registerRaw(user);
        requireStatus(response,"Register user or check if registered", 201,422);
        if (response.getStatusCode() == 422) {
            response.then().body("errors.collect { it.value }.flatten()",
                    hasItem(containsString("already exists")));
        }
        if(response.getStatusCode()!= 422) {
            httpRestClient.setJwtToken(response.jsonPath().getObject("user", UserResponse.class).getToken());
        }

    }

    public void setRawAuthorizationHeader(String jwtToken) {
        httpRestClient.setRawAuthorizationHeader(jwtToken);
    }

    public void setExpiredAuthorizationHeader(String email) {
        httpRestClient.setJwtToken(JwtTokenFactory.expiredToken(email));
    }

    public void getCurrentUser() {
        httpRestClient.request(Method.GET, GET_CURRENT_USER_ENDPOINT).send();
    }


}
