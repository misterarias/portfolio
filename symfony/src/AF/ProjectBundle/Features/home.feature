@project
Feature: Homepage elements
  In order to present my project
  As a web user
  I need to be able to navigate the project
  And I expect to find basic elements

  Scenario Outline: Navigation bar
    Given I am on "/"
    Then I should find a navigation bar with 3 items
    Then I should find a navigation bar entry named '<module>' with link '<link>'
  Examples:
    | module     | link     |
    | Home       | /        |
    | Scraper UI | /scraper |
    | Data UI    | /data    |
