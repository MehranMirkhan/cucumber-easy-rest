Feature: MVC feature

  Scenario: MVC
    When GET /public/meta/health
    Then status is 200
    And $.status = UP
    When GET /public/meta/health -H lang=en
    Then status is 200
    And $.status = UP
    And $.lang = en
    And $.cookie is null
    And $.load > 0.1
    When GET /public/meta/health:
      | name   |
      | Mehran |
    Then status is 200
    And $.status = UP
    And $.body.name = Mehran
    And $.body.gender does not exist
    When GET /public/meta/health -H lang=en -H cookie=abcd:
      | name   | gender |
      | Mehran | MALE   |
    Then status is 200
    And $.status = UP
    And $.lang = en
    And $.cookie = abcd
    And $.cookie ends_with_ic CD
    And $.cookie contains_ic Bc
    And $.body.name = Mehran
    And $.body.gender = MALE

  Scenario: Upload file
    When POST /public/file/upload -F[myFile] text.txt=Hello, World!
    Then status is 200
    And $ = Hello, World!
    When POST /public/file/upload -F[myFile] text.txt=0x48656C6C6F2C20576F726C6421
    Then status is 200
    And $ = Hello, World!
