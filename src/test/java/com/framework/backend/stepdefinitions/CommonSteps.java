package com.framework.backend.stepdefinitions;

import com.framework.backend.context.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    private static final String SCHEMA_DIR = "schemas/";

    private final CommonContext commonContext;

    public CommonSteps(CommonContext commonContext) {
        this.commonContext = commonContext;
    }

    @Then("Response field {string} equals {string}")
    public void responseFieldEquals(String jsonPath, String expectedValue) {
        assertThat(commonContext.getResponse().jsonPath().getString(jsonPath))
                .as("Response field '%s'", jsonPath)
                .isEqualTo(expectedValue);
    }

    @Then("Response matches schema {string}")
    public void responseMatchesSchema(String schemaFile) {
        commonContext.getResponse().then().assertThat().body(matchesJsonSchemaInClasspath(SCHEMA_DIR + schemaFile));
    }

    @And("the response error should contain {string}")
    public void theResponseErrorShouldContain(String expectedError) {
        Response response = commonContext.getResponse();
        Map<String, Object> errors = response.jsonPath().getMap("errors");
        assertThat(errors).as("errors in body: %s", response.asString()).isNotNull();

        List<String> messages = new ArrayList<>();
        errors.values().forEach(value -> {
            if (value instanceof Collection) {
                ((Collection<?>) value).forEach(message -> messages.add(String.valueOf(message)));
            } else {
                messages.add(String.valueOf(value));
            }
        });
        assertThat(messages)
                .withFailMessage("Expected error \"%s\" but the response returned %s", expectedError, messages)
                .contains(expectedError);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertThat(commonContext.getResponseStatusCode())
                .as("Response status code. Body: %s", commonContext.getResponse().asString())
                .isEqualTo(expectedStatusCode);
    }
}
