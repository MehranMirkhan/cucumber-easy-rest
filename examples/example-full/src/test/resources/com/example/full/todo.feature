Feature: Todo

  Background:
    Given username <- &rand_a(4)
    Given find Role:
      | name      |
      | ROLE_USER |
    Given insert records for User:
      | username    | password | roles           |
      | &(username) | 123456   | [{"id": &(id)}] |
    Given user &(username) with role ROLE_USER

  Scenario: CRUD
    When GET /todo
    Then status is 200
    And $.content is empty
    And $.content has_size 0
    When POST /todo:
      | text  | done  |
      | task1 | false |
    Then status is 201
    Given todoId <- $.id
    When GET /todo/&(todoId)
    Then status is 200
    And $.text = task1
    And $.done = false
    When GET /todo
    Then status is 200
    And $.content is not empty
    And $.content has_size 1
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

  Scenario: Upsert record
    When POST /todo:
      | text        | done |
      | Buy grocery | true |
    Then status is 201
    Given todoId <- $.id
    When GET /todo/&(todoId)
    Then status is 200
    And $.text = Buy grocery
    And $.done = true
    Given upsert records for Todo:
      | id        | text        | done  |
      | &(todoId) | Do homework | false |
    When GET /todo/&(todoId)
    Then status is 200
    And $.text = Do homework
    And $.done = false

  Scenario: Delete record
    When POST /todo:
      | text        | done  |
      | Buy grocery | false |
    Then status is 201
    Given todoId <- $.id
    When GET /todo/&(todoId)
    Then status is 200
    Given delete records for Todo:
      | id        |
      | &(todoId) |
    When GET /todo/&(todoId)
    Then status is 404
