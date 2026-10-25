@api @auth @login
Feature: User Authentication (Login)
  As a registered Conduit user
  I want to log into my account
  So that I can access authenticated endpoints and manage my content

  Background:
    Given an existing registered user exists with credentials:
      | email              | password      | username    |
      | testLogin@mail.com | testLogin123! | LoginTester |

  @smoke
  Scenario: Successfully log in with valid credentials
    Given I have a login payload with:
      | email              | password      |
      | testLogin@mail.com | testLogin123! |
    When I send a request to log in
    Then the response status code should be 200
    And Response matches schema "user-response.json"
    And the response should contain a valid JWT token
    And the response user details should be:
      | email              | username    |
      | testLogin@mail.com | LoginTester |
    And the user password should not be returned in the response

  @known-issue
  Scenario Outline: Fail login when required fields are missing
    Given I have a login payload missing the "<missing_field>" field
    When I send a request to log in
    Then the response status code should be 422
    And the response error should contain "<expected_error>"

    Examples:
      | missing_field | expected_error          |
      | email         | email can't be blank    |
      | password      | password can't be blank |

  @known-issue
  Scenario Outline: Fail login with invalid credentials
    Given I have a login payload with:
      | email   | password   |
      | <email> | <password> |
    When I send a request to log in
    Then the response status code should be 422
    And the response error should contain "Wrong email/password combination"

    Examples:
      | email                            | password      | description              |
      | testLogin@mail.com               | wrongpass123  | Incorrect password       |
      | testLogin_non_existent@jake.jake | testLogin123! | Unregistered email       |
      | testLogin@mail.com               | TestLogin123! | Case-mismatched password |

  @known-issue
  Scenario: Fail login with malformed email format
    Given I have a login payload with:
      | email     | password      |
      | testLogin | testLogin123! |
    When I send a request to log in
    Then the response status code should be 422
    And the response error should contain "email is invalid"