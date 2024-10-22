Feature: MVC feature

  Scenario: MVC
    When GET /public/meta/health
    Then status is 200
    And $.status = UP
    When GET /public/meta/health -H lang=en
    Then status is 200
    And $.status = UP
    And $.lang = en
    And $.cookie is empty
    When GET /public/meta/health:
      | name   |
      | Mehran |
    Then status is 200
    And $.status = UP
    And $.body.name = Mehran
    And $.body.gender is null
    When GET /public/meta/health -H lang=en -H cookie=abcd:
      | name   | gender |
      | Mehran | MALE   |
    Then status is 200
    And $.status = UP
    And $.lang = en
    And $.cookie = abcd
    And $.body.name = Mehran
    And $.body.gender = MALE
