Feature: Todo

  Background:
    Given username <- &rand_a(4)
    Given admin
    When POST /admin/user:
      | username    | password | roles         |
      | &(username) | 123456   | ["ROLE_USER"] |
    Then status is 201
    Given userId <- $.id
    Given user &(username) with role ROLE_USER

  Scenario: CRUD
    When GET /todo
    Then status is 200
    And $.content is empty
    And $.content has_size 0
    When POST /todo:
      | text  | done  | priority |
      | task1 | false | &int(3)  |
    Then status is 201
    Given todoId <- $.id
    When GET /todo/&(todoId)
    Then status is 200
    And $.text = task1
    And $.done = false
    And $.priority = 3
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

  Scenario: Mock getById
    Given mock todoService.getById(any):
      | id   | text        | done  |
      | 1000 | Mocked Todo | false |
    Given user admin with role ROLE_ADMIN
    When GET /admin/todo/1000
    Then status is 200
    And $.text = Mocked Todo

  Scenario: Mock delete
    Given mock todoService.delete(any)
    Given user admin with role ROLE_ADMIN
    When DELETE /admin/todo/1000
    Then status is 200

  Scenario: No Mock
    When POST /todo:
      | text        | done |
      | Buy grocery | true |
    Then status is 201
    Given todoId <- $.id
    Given user admin with role ROLE_ADMIN
    When GET /admin/todo/&(todoId)
    Then status is 200
    And $.text = Buy grocery

  Scenario: Directly change DB
    Given insert records for Todo:
      | text          | done  | priority | owner.id  |
      | From Database | false | 5        | &(userId) |
    When GET /todo
    Then status is 200
    * $.content has_size 1
    * $.content[0].text = From Database
    * $.content[0].priority = 5
    * $.content[0].done = false
