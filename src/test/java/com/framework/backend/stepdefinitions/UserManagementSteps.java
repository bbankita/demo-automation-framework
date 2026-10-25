package com.framework.backend.stepdefinitions;

import com.framework.backend.api.UserApi;
import com.framework.backend.context.UserContext;
import com.framework.backend.model.User;
import com.framework.backend.utilities.UserQueries;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class UserManagementSteps {

    private final UserApi userApi;
    private final UserContext userContext;
    private final UserQueries userQueries;

    public UserManagementSteps(UserApi userApi, UserContext userContext, UserQueries userQueries) {
        this.userApi = userApi;
        this.userContext = userContext;
        this.userQueries = userQueries;
    }

    @Given("I am authenticated as {string} with password {string}")
    public void iAmAuthenticatedAsWithPassword(String email, String password) {
        User user = User.builder().email(email).password(password).build();
        userApi.login(user);
        userContext.setCurrentUser(user);
    }


    @When("I send a request to get the current user")
    public void iSendARequestToGetTheCurrentUser() {
        userApi.getCurrentUser();
    }

    @Given("I set the Authorization header to {string}")
    public void iSetTheAuthorizationHeaderTo(String authorizationHeader) {
        userApi.setRawAuthorizationHeader(authorizationHeader);
    }

    @Given("I have an expired JWT token for user {string}")
    public void iHaveAnExpiredJWTTokenForUser(String email) {
        userApi.setExpiredAuthorizationHeader(email);
    }

    @And("the user account {string} is deleted from the database")
    public void theUserAccountIsDeletedFromTheDatabase(String userEmail) {
        userQueries.deleteUserByEmail(userEmail);
    }
}
