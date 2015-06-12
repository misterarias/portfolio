@bio
Feature: Page Elements
  In order to showoff
  As a web user
  I need to be able to navigate the bio site
  And I expect to find basic elements

  Scenario: Navigation bar
    Given I am on "/"
    Then I should find a navigation bar with 6 items
