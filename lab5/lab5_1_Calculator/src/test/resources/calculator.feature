Feature: Basic Arithmetic

  Background: A Calculator
    Given a calculator I just turned on

  Scenario: Addition
    When I add 4 and 5
    Then the result is 9

  Scenario: Subtraction
    When I subtract 7 to 2
    Then the result is 5

  Scenario Outline: Several additions
    When I add <a> and <b>
    Then the result is <c>

    Examples: Single digits
      | a | b | c  |
      | 1 | 2 | 3  |
      | 3 | 7 | 10 |

  Scenario: Multiplication of two numbers
    Given a calculator I just turned on
    When I multiply 3 and 5
    Then the result is 15

  Scenario: Division of two numbers
    Given a calculator I just turned on
    When I divide 10 by 2
    Then the result is 5

  Scenario: Division by zero
    Given a calculator I just turned on
    When I divide 10 by 0
    Then the result is error

  Scenario: Invalid operation
    Given a calculator I just turned on
    When I perform "@" with 5 and 3
    Then the result is error