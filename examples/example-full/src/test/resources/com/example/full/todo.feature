Feature: Todo

  Scenario: CRUD
    Given find Role:
      | name      |
      | ROLE_USER |
    Given records for User:
      | username | password | roles           |
      | u1       | 123456   | [{"id": &(id)}] |
    Given user u1 with role ROLE_USER
    When GET /todo
    Then status is 200
    And $.content = []
    When POST /todo:
      | text  | done  |
      | task1 | false |
    Then status is 201
    Given Set todoId = $.id
    When GET /todo/&(todoId)
    Then status is 200
    And $.text = task1
    And $.done = false
    When PUT /todo/&(todoId):
      | text  | done |
      | task2 | true |
    Then status is 200
    When GET /todo/&(todoId)
    Then status is 200
    And $.text = task2
    And $.done = true
    When DELETE /todo/&(todoId)
    Then status is 200
    When GET /todo/&(todoId)
    Then status is 404
