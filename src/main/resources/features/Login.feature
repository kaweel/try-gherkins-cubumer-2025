Feature: Login API

    Scenario: Login failed with invalid username
        Given the user has an invalid username
        When the user sends a login request
        Then the system should return an error response

    Scenario: Login failed with invalid password
        Given the user has an invalid password
        When the user sends a login request
        Then the system should return an error response

    # @Skip
    Scenario: Login successfully
        Given the user has valid credential
        When the user sends a login request
        Then the system should return a success response