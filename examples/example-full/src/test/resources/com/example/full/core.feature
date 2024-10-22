Feature: Core feature

  Scenario: Context & Rand
    Then correct
    Given Set a1 = 1
    Given Set a2 = &(a1)
    Then &(a1) = &(a2)
    Then 2 != &(a2)
    Given Set r1 = &rand()
    Given Set r2 = &rand_n(4)
    Then &(r1) < &(r2)
    Then &(r1) <= 1.0
    Then &(r2) > 31
    Then &(r2) >= 99

  Scenario: Context & String
    Given Set str1 = MeHrAn
    Then &lower(&(str1)) = mehran
    Given Set str2 = MEHRAN
    Then &(str2) = &upper(&(str1))
