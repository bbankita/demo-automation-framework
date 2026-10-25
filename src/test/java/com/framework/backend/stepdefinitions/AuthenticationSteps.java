package com.framework.backend.stepdefinitions;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.framework.backend.api.UserApi;
import com.framework.backend.context.CommonContext;
import com.framework.backend.context.UserContext;
import com.framework.backend.model.User;
import com.framework.backend.model.UserResponse;
import com.framework.backend.utilities.JwtTokenParser;
import com.framework.common.utilities.LogHelper;
import com.framework.common.utilities.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class AuthenticationSteps {

    private final UserApi userApi;
    private final UserContext userContext;
    private final CommonContext commonContext;

    private User payload;

    public AuthenticationSteps(UserApi userApi, UserContext userContext, CommonContext commonContext) {
        this.userApi = userApi;
        this.userContext = userContext;
        this.commonContext = commonContext;
    }

    @Given("I have a new user payload with valid details")
    public void iHaveANewUserPayloadWithValidDetails() {
        payload = TestDataFactory.randomConduitUser();
    }

    @When("I send a request to register the user")
    public void iSendARequestToRegisterTheUser() {
        userApi.registerRaw(payload);
        userContext.setCurrentUser(payload);
    }

    @And("the response should contain a valid JWT token")
    public void theResponseShouldContainAValidJWTToken() {
        UserResponse userResponse = commonContext.getResponseAs("user", UserResponse.class);
        String token = userResponse.getToken();
        DecodedJWT jwt = JwtTokenParser.decode(token);
        Instant now = Instant.now();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(jwt.getClaim("email").asString())
                .as("JWT claim 'email'")
                .isEqualTo(userResponse.getEmail());
        softly.assertThat(jwt.getExpiresAtAsInstant())
                .as("JWT claim 'exp'")
                .isCloseTo(now.plus(Duration.ofDays(7)), within(1, ChronoUnit.MINUTES));
        softly.assertThat(jwt.getIssuedAtAsInstant())
                .as("JWT claim 'iat'")
                .isBeforeOrEqualTo(now.plus(Duration.ofSeconds(30)));
        softly.assertAll();
    }

    @And("the response user details should match the registered email and username")
    public void theResponseUserDetailsShouldMatchTheRegisteredEmailAndUsername() {
        User registered = userContext.getCurrentUser();
        UserResponse response = commonContext.getResponseAs("user", UserResponse.class);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getEmail()).as("user.email").isEqualTo(registered.getEmail());
        softly.assertThat(response.getUsername()).as("user.username").isEqualTo(registered.getUsername());
        softly.assertAll();
    }

    @Given("I have a registration payload missing the {string} field")
    public void iHaveARegistrationPayloadMissingTheField(String field) {
        User.Builder builder = TestDataFactory.randomConduitUser().toBuilder();
        switch (field) {
            case "email":
                builder.email(null);
                break;
            case "username":
                builder.username(null);
                break;
            case "password":
                builder.password(null);
                break;
            default:
                throw new IllegalArgumentException("Unknown registration field: " + field);
        }
        payload = builder.build();
    }

    @Given("a user already exists with email {string} and username {string}")
    public void aUserAlreadyExistsWithEmailAndUsername(String email, String username) {
        User user = TestDataFactory.randomConduitUser().toBuilder().email(email).username(username).build();
        userApi.ensureRegisteredUser(user);
    }

    @When("I attempt to register another user with email {string}")
    public void iAttemptToRegisterAnotherUserWithEmail(String email) {
        userApi.registerRaw(TestDataFactory.randomConduitUser().toBuilder().email(email).build());
    }

    /**
     * Same as the table step, but the credentials come from {@code users.<userKey>} in config/<env>.json.
     */
    @Given("the configured user {string} is registered")
    public void theConfiguredUserIsRegistered(String userKey) {
        User user = TestDataFactory.configuredUser(userKey);
        LogHelper.info("Logging in as " + user.getEmail());
        userApi.ensureRegisteredUser(user);
        userContext.setCurrentUser(user);
    }

    @Given("I have a login payload with:")
    public void iHaveALoginPayloadWith(List<Map<String, String>> payloadRows) {
        Map<String, String> row = payloadRows.get(0);
        payload = User.builder()
                .email(row.get("email"))
                .password(row.get("password"))
                .build();
    }

    @When("I send a request to log in")
    public void iSendARequestToLogIn() {
        userApi.loginRaw(payload);
        userContext.setCurrentUser(payload);
    }

    @And("the response user details should be:")
    public void theResponseUserDetailsShouldBe(List<Map<String, String>> payloadRows) {
        Map<String, String> row = payloadRows.get(0);
        UserResponse response = commonContext.getResponseAs("user", UserResponse.class);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getEmail()).as("user.email").isEqualTo(row.get("email"));
        softly.assertThat(response.getUsername()).as("user.username").isEqualTo(row.get("username"));
        softly.assertAll();
    }

    @And("the user password should not be returned in the response")
    public void theUserPasswordShouldNotBeReturnedInTheResponse() {
        Response response = commonContext.getResponse();
        assertThat(response.jsonPath().getString("user.password"))
                .as("user.password")
                .isNull();
    }

    @Given("I have a login payload missing the {string} field")
    public void iHaveALoginPayloadMissingTheField(String missingField) {
        User.Builder builder = TestDataFactory.randomConduitUser().toBuilder();
        builder.username(null); // Username is not used for login, so always null
        switch (missingField) {
            case "email":
                builder.email(null);
                break;
            case "password":
                builder.password(null);
                break;
        }
        payload = builder.build();
    }

    @Given("an existing registered user exists with credentials:")
    public void anExistingRegisteredUserExistsWithCredentials(List<User> users) {
        users.forEach(userApi::ensureRegisteredUser);
    }

}
