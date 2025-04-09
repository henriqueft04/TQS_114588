Feature: User Management
  As a system administrator
  I want to manage users in the system
  So that different types of users can access appropriate functionality

  Scenario: Create a new user
    When I create a user with the following details:
      | Email           | Name        | Role      |
      | john@test.com   | John Smith  | CUSTOMER  |
    Then the user should be created successfully
    And the user's role should be "CUSTOMER"

  Scenario: Find a user by email
    Given a user exists with email "jane@test.com" and name "Jane Doe" and role "STAFF"
    When I search for a user with email "jane@test.com"
    Then I should find a user with name "Jane Doe"

  Scenario: List users by role
    Given the following users exist:
      | Email            | Name          | Role      |
      | admin@test.com   | Admin User    | ADMIN     |
      | staff1@test.com  | Staff One     | STAFF     |
      | staff2@test.com  | Staff Two     | STAFF     |
      | cust1@test.com   | Customer One  | CUSTOMER  |
    When I search for users with role "STAFF"
    Then I should find 2 users
    And the results should include "Staff One" and "Staff Two" 