Feature: Reservation Processing
  As a restaurant customer
  I want my reservation to be properly processed
  So that I can have a guaranteed table when I arrive

  Background:
    Given the restaurant "Gourmet Place" exists with capacity 100
    And the restaurant has a dinner service from "18:00" to "22:00" on "FRIDAY"

  Scenario: Make a standard reservation
    When I make a reservation with the following details:
      | Customer Name | Customer Email      | Customer Phone | Party Size | Reservation Time | Meal Type |
      | John Doe      | john@example.com    | 1234567890     | 4          | 19:00           | Dinner    |
    Then the reservation should be created successfully
    And I should receive a confirmation token
    And the reservation status should be "PENDING"

  Scenario: Process a reservation for a large group
    When I make a reservation with the following details:
      | Customer Name | Customer Email      | Customer Phone | Party Size | Reservation Time | Meal Type |
      | Big Party     | party@example.com   | 1234567890     | 12         | 19:00           | Dinner    |
    Then the reservation should be created successfully
    And the reservation should be marked as a group reservation

  Scenario: Reject reservation for exceeded capacity
    Given 90 seats are already reserved for "19:00" on "FRIDAY"
    When I make a reservation with the following details:
      | Customer Name | Customer Email      | Customer Phone | Party Size | Reservation Time | Meal Type |
      | Late Booker   | late@example.com    | 1234567890     | 15         | 19:00           | Dinner    |
    Then the reservation should fail
    And I should receive an error message about insufficient capacity 