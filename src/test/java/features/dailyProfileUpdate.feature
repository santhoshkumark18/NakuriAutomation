@profileUpdate
Feature: Daily Naukri Profile Update Automation
  
  Background:
    Given I navigate to Naukri Application for daily update
    And I login with environment credentials
    
  @dailyProfileUpdate
  Scenario: Daily profile update with location and resume
    When I navigate to my profile page
    And I update career profile with random location selection
    And I upload the latest resume
    And I verify the profile activity is recorded
