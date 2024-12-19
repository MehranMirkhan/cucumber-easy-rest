Feature: User CRUD

  Scenario: Success Path
    Given user:
      | username | roles          |
      | admin    | ["ROLE_ADMIN"] |
    When GET /admin/user
    Then status is 200
    And $.content is not empty
    And $.content[0].deleted = false
    And $.content[0].deleted == &bool(false)
    When POST /admin/user:
      | username | password | roles         |
      | john     | 123456   | ["ROLE_USER"] |
    Then status is 201
    Given userId <- $.id
    When GET /admin/user/&(userId)
    Then status is 200
    And $.username = john
    And $.disabled = false
    Given user john with role ROLE_USER
    When GET /user
    Then status is 200
    And $.username = john
    And $.disabled = false
    When GET /admin/user
    Then status is 403
    Given admin
    When PUT /admin/user/&(userId):
      | password | disabled | roles         |
      | 123456   | true     | ["ROLE_USER"] |
    Then status is 200
    When GET /admin/user/&(userId)
    Then status is 200
    And $.disabled = true
    When DELETE /admin/user/&(userId)
    Then status is 200
    When GET /admin/user/&(userId)
    Then status is 200
    And $.deleted = true
