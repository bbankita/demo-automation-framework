package com.framework.frontend.page;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.backend.model.UserResponse;
import com.framework.common.utilities.LogHelper;
import com.framework.exceptions.FrameworkException;
import com.framework.frontend.utilities.BrowserSession;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import static com.framework.common.utilities.ConfigurationReader.getConfigurations;


public class HomePage extends BasePage{

    private static final By homeButton = By.xpath("//a[contains(normalize-space(),'Home')]");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public HomePage(BrowserSession browserSession) {
        super(browserSession);
    }

    public void open() {
        LogHelper.info("[Browser] Opening " + getConfigurations().getWebUrl());
        webDriver.get(getConfigurations().getWebUrl());
        waitForVisible(homeButton, "home button");
    }

    public void openAsLoggedIn(UserResponse userResponse) {
        open();
        LogHelper.info("[Browser] Injecting logged-in user into localStorage");
        String script = "var user = JSON.parse(arguments[0]);\n" +
                "window.localStorage.setItem('loggedUser', JSON.stringify({\n" +
                "    headers: { Authorization: 'Token ' + user.token },\n" +
                "    isAuth: true,\n" +
                "    loggedUser: user\n" +
                "}));";
        ((JavascriptExecutor) webDriver).executeScript(script, toJson(userResponse));
        webDriver.navigate().refresh();
    }

    private static String toJson(UserResponse userResponse) {
        try {
            return OBJECT_MAPPER.writeValueAsString(userResponse);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("Could not serialize logged-in user " + userResponse.getEmail(), e);
        }
    }
}
