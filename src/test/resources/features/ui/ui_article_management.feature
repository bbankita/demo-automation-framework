@ui @conduit @articles
Feature: Article authoring
  As an author
  I want to write, edit and delete my articles

  Background:
    Given I am authenticated as user "standardConduitUser"

  @smoke
  Scenario: Author publishes an article with tags
    Given I am on the Editor page
    And I publish an article with:
      | title    | description   | body                                 | tags             |
      | <unique> | Short summary | Body in **markdown** with *emphasis* | selenium,testing |
    Then the article page should show the title, author as "standardConduitUser" and rendered body
    And the article should display the tags:
      | selenium |
      | testing  |

  Scenario: Author edits an existing article
    Given I have published an article
    And I am on the Article Page
    When I change the article title to a new unique title
    Then the article page should show the new title
    And the article should be reachable at its updated URL

#  Scenario: Author deletes an article
#    Given the user has published an article via the API
#    When the user deletes the article from the article page
#    Then the user should be redirected to the home page
#    And the article should no longer appear in the user's profile
#
#  Scenario: Publishing without a title shows a validation error
#    When the user tries to publish an article without a title
#    Then an error message should be displayed
#    And the user should remain on the editor page
#
#  Scenario: Non-author does not see edit or delete controls
#    Given another user has published an article via the API
#    When the user opens that article
#    Then the "Edit Article" and "Delete Article" buttons should not be displayed