@api @auth @registration
Feature: Registration API
  As a new user
  I want to register for a Conduit account
  So that I can log in and publish articles

  @smoke
  Scenario: Successfully register a new user with valid credentials
    Given I have a new user payload with valid details
    When I send a request to register the user
    Then the response status code should be 201
    And Response matches schema "user-response.json"
    And the response should contain a valid JWT token
    And the response user details should match the registered email and username

  Scenario Outline: Fail registration when required fields are missing
    Given I have a registration payload missing the "<field>" field
    When I send a request to register the user
    Then the response status code should be 422
    And the response error should contain "<error>"
    Examples:
      | field    | error                  |
      | email    | An email is required   |
      | username | A username is required |
      | password | A password is required |

  Scenario: Fail registration when email or username is already taken
    Given an existing registered user exists with credentials:
      | email              | password     | username   |
      | test-user@mail.com | testUser123! | Authtester |
    When I attempt to register another user with email "test-user@mail.com"
    Then the response status code should be 422
    And the response error should contain "Email already exists.. try logging in"
