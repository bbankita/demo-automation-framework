package com.framework.frontend.stepdefinitions;

import com.framework.backend.api.UserApi;
import com.framework.backend.model.User;
import com.framework.common.utilities.LogHelper;
import com.framework.common.utilities.TestDataFactory;
import com.framework.frontend.page.HomePage;
import io.cucumber.java.en.Given;

public class CommonSteps {

    UserApi userApi;
    HomePage homePage;

    public CommonSteps(UserApi userApi, HomePage homePage) {
        this.userApi = userApi;
        this.homePage = homePage;
    }

    @Given("I am authenticated as user {string}")
    public void iAmAuthenticatedAsUser(String userKey) {
        User user = TestDataFactory.configuredUser(userKey);
        LogHelper.info("Logging in as " + user.getEmail());
        homePage.openAsLoggedIn(userApi.login(user));
    }

}
