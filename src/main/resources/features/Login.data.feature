Feature: Login Example Data API

    Scenario Outline: Login attempt with different credentials using example data
        Given the user has the following credentials username "<username>" and password "<password>"
        When the user sends a data login request
        Then the system should return response status "<status>"

        Examples:
            | username  | password  | status       |
            | kaweel    | h@ndS0m3  | OK           |
            | kaweel    | wrongpass | UNAUTHORIZED |
            | wronguser | h@ndS0m3  | UNAUTHORIZED |