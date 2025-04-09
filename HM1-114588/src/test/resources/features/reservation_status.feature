Feature: Reservation Status Management
  As a restaurant staff member
  I want to manage reservation statuses
  So that I can handle customer arrivals effectively

  Background: 
    Given the restaurant "Status Test" exists with capacity 50
    And the restaurant has a dinner service from "18:00" to "22:00" on "SATURDAY"

  Scenario: Confirm a pending reservation
    Given a reservation exists for "John Doe" with token "ABC123"
    When I confirm the reservation with token "ABC123"
    Then the reservation status should be "CONFIRMED"
    And the confirmation time should be recorded

  Scenario: Customer checks in with token
    Given a confirmed reservation exists for "Jane Smith" with token "DEF456"
    When the customer checks in with token "DEF456"
    Then the reservation status should be "CHECKED_IN"

  Scenario: Mark a reservation as completed
    Given a checked-in reservation exists for "Bob Johnson" with token "GHI789"
    When I mark the reservation as "COMPLETED"
    Then the reservation status should be "COMPLETED"

  Scenario: Mark a no-show reservation
    Given a confirmed reservation exists for "No Show Guest" with token "JKL012"
    And the reservation time has passed
    When I mark the reservation as "NO_SHOW"
    Then the reservation status should be "NO_SHOW"

  Scenario: Failed confirmation with invalid token
    Given a reservation exists for "John Doe" with token "ABC123"
    When I confirm the reservation with token "INVALID"
    Then the confirmation should fail
    And I should receive an error message about invalid token 