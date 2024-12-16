Feature: Core feature

  Scenario: Context & Rand
    Then correct
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
