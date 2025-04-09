Feature: Restaurant Reservation Management
  As a customer
  I want to make and manage restaurant reservations
  So that I can plan my dining experience

  Background:
    Given the restaurant "Sample Restaurant" exists with capacity 50
    And the restaurant is open from "10:00" to "22:00"

  Scenario: Successful reservation creation
    When I make a reservation with the following details:
      | Customer Name | Customer Email      | Customer Phone | Party Size | Reservation Time | Meal Type |
      | John Doe     | john@example.com    | 1234567890    | 4          | 19:00           | Dinner    |
    Then the reservation should be created successfully
    And I should receive a confirmation token
    And the reservation status should be "PENDING"

  Scenario: Failed reservation due to invalid party size
    When I make a reservation with the following details:
      | Customer Name | Customer Email      | Customer Phone | Party Size | Reservation Time | Meal Type |
      | John Doe     | john@example.com    | 1234567890    | 51         | 19:00           | Dinner    |
    Then the reservation should fail
    And I should receive an error message about invalid party size

  Scenario: Successful reservation confirmation
    Given a reservation exists for "John Doe" with token "ABC123"
    When I confirm the reservation with token "ABC123"
    Then the reservation status should be "CONFIRMED"
    And the confirmation time should be recorded

  Scenario: Failed reservation confirmation with invalid token
    Given a reservation exists for "John Doe" with token "ABC123"
    When I confirm the reservation with token "INVALID"
    Then the confirmation should fail
    And I should receive an error message about invalid token 