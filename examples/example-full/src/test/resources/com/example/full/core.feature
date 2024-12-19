Feature: Core feature

  Scenario: Context & Rand
    Then correct
    And 3 = 3
    # Check with type
    And 3 == &int(3)
    And 3.0 == &double(3)
    Given a1 <- 1
    Given a2 <- &(a1)
    Then &(a1) = &(a2)
    Then 2 != &(a2)
    Given r1 <- &rand()
    Given r2 <- &rand_n(4)
    Then &(r1) < &double(&(r2))
    Then &(r1) <= 1.0
    Then &(r2) > 31
    Then &(r2) >= 99

  Scenario: Context & String
    Given str1 <- MeHrAn
    Then &lower(&(str1)) = mehran
    Given str2 <- MEHRAN
    Then &(str2) = &upper(&(str1))

  Scenario: Math
    Then 3 = &add(1,2)
    And 3 == &int(&add(1,2))
    And 3.1 = &add(1,2.1)
    Given a <- 12.2
    Given b <- 5
    Then &mul(&(a),&(b)) = 61.0
    And &div(&div(&div(64,2),2),2) = 8
    And &div(&div(&div(64.0,2),2),2) = 8.0
