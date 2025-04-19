Feature: Login Datatable API

    Scenario Outline: Login attempt with different credentials using datatable
        Given the user has the following each credentials
            | username | <username> |
            | password | <password> |
        When the user sends each data login request
        Then the system should return each response status "<status>"

        Examples:
            | username  | password  | status       |
            | kaweel    | h@ndS0m3  | OK           |
            | kaweel    | wrongpass | UNAUTHORIZED |
            | wronguser | h@ndS0m3  | UNAUTHORIZED |
