package com.framework.steps.ui;

import com.framework.api.UserApi;
import com.framework.model.User;
import com.framework.core.logging.LogHelper;
import com.framework.utilities.TestDataFactory;
import com.framework.ui.HomePage;
import io.cucumber.java.en.Given;

public class UiAuthenticationSteps {

    UserApi userApi;
    HomePage homePage;

    public UiAuthenticationSteps(UserApi userApi, HomePage homePage) {
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
