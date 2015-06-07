@project
Feature: Scraper UI Elements
  In order to review my scraping
  As a web user
  I need to be able to see a main graph
  And I expect to see controls to manipulate it
  And I expect to see statistics on my scraping

  @javascript
  Scenario: Main graph
    Given I am on "/scraper"
    Then I should find a graph
