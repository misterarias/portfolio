@project
Feature: Data UI Elements
  In order to present my results
  As a web user
  I need to be able to see a main graph
  And I expect to see controls to manipulate it

  @javascript
  Scenario: Main graph
    Given I am on "/data"
    Then I should find a graph
