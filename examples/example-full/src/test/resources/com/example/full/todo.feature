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
    And $.content is empty
    And $.content has size 0
    When POST /todo:
      | text  | done  |
      | task1 | false |
    Then status is 201
    Given Set todoId = $.id
    When GET /todo/&(todoId)
    Then status is 200
    And $.text = task1
    And $.done = false
    When GET /todo
    Then status is 200
    And $.content is not empty
    And $.content has size 1
    And $.numberOfElements = 1
    And $.numberOfElements > 0
    And $.content has a match text = task1
    And $.content has a match text starts_with task
    And $.content has a match text starts_with_ic TaSk
    And $.content has no match done = &bool(true)
    And $.content all match done = &bool(false)
    And $.content has a match id = &int(1)
    And $.content has a match id > 0
    And $.content[?(@.id==1)] exists
    And $.content[?(@.id>50)] does not exist
    When PUT /todo/&(todoId):
      | text  | done |
      | task2 | true |
    Then status is 200
    When GET /todo/&(todoId)
    Then status is 200
    And $.text = task2
    And $.text starts_with task
    And $.done = true
    When DELETE /todo/&(todoId)
    Then status is 200
    When GET /todo/&(todoId)
    Then status is 404
