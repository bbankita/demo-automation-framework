@api @user-management
Feature: Get Current User Details
  As an authenticated Conduit user
  I want to fetch my own user profile details
  So that I can load my account state and session in the frontend application

  Background:
    Given an existing registered user exists with credentials:
      | email                          | password        | username           |
      | testCurrentUser@mail.com       | testCurrent123! | LoginTCurrent      |
      | testCurrentUserDelete@mail.com | testCurrent123! | LoginCurrentDelete |

  @smoke
  Scenario: Successfully retrieve current user profile with a valid token
    Given I am authenticated as "testCurrentUser@mail.com" with password "testCurrent123!"
    When I send a request to get the current user
    Then the response status code should be 200
    And the response user details should be:
      | email                    | username      |
      | testCurrentUser@mail.com | LoginTCurrent |
    And the response should contain a valid JWT token
    And the user password should not be returned in the response

  @known-issue
  Scenario Outline: Fail to retrieve current user when authorization header is missing or invalid
    Given I set the Authorization header to "<auth_header>"
    When I send a request to get the current user
    Then the response status code should be 401
    And the response error should contain "Token missing or malformed"

    Examples:
      | auth_header              | description                         |
      |                          | Missing Authorization header        |
      | Token                    | Missing token payload               |
      | Token invalid_jwt_string | Malformed or corrupted JWT token    |
      | Bearer valid_jwt_token   | Invalid authorization scheme prefix |

  Scenario: Fail to retrieve current user when the JWT token is expired
    Given I have an expired JWT token for user "testCurrentUser@mail.com"
    When I send a request to get the current user
    Then the response status code should be 401
    And the response error should contain "Token expired, please login again"

  @known-issue
  Scenario: Fail to retrieve current user if the user account was deleted after token issuance
    Given I am authenticated as "testCurrentUserDelete@mail.com" with password "testCurrent123!"
    And the user account "testCurrentUserDelete@mail.com" is deleted from the database
    When I send a request to get the current user
    Then the response status code should be 404
    And the response error should contain "User not found"