@integration
Feature: Page Elements
  In order to present my project
  As a web user
  I need to be able to navigate the project
  And I expect to find basic elements

  Scenario: Navigation bar
    Given I am on "/project"
    Then I should find a navigation bar with 3 items