Feature: Restaurant Management
  As a restaurant owner
  I want to manage my restaurant details
  So that I can provide accurate information to customers

  Scenario: Create a new restaurant
    When I create a restaurant with name "Fine Dining" and capacity 70
    Then the restaurant should be created successfully
    And the restaurant should have a capacity of 70

  Scenario: Update restaurant capacity
    Given the restaurant "Bella Italia" exists with capacity 50
    When I update the capacity to 60
    Then the restaurant should have a capacity of 60

  Scenario: Delete a restaurant
    Given the restaurant "To Be Deleted" exists with capacity 30
    When I delete the restaurant
    Then the restaurant should no longer exist

  Scenario: Add meal service to restaurant
    Given the restaurant "Fine Dining" exists with capacity 80
    When I add a meal service with the following details:
      | Meal Type  | Day       | Start Time | End Time |
      | DINNER     | FRIDAY    | 18:00      | 23:00    |
    Then the restaurant should have a "DINNER" service on "FRIDAY"
    And the service hours should be from "18:00" to "23:00" 