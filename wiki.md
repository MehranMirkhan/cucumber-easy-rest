# Cucumber Easy REST

[Cucumber](https://cucumber.io/) is a tool that enables you to write tests in a human-readable format.
However, it does not provide any built-in semantics for test steps—only pattern matching—leaving the implementation of test logic entirely up to the test writer.

This project addresses that gap by offering a set of predefined steps specifically designed for testing REST endpoints.
With these ready-to-use steps, you can focus on writing meaningful test scenarios without worrying about the underlying implementation details.

This document will guide you through installation and usage of this library. You can directly see [examples](https://github.com/MehranMirkhan/cucumber-easy-rest/tree/main/examples) for detailed usage code.

## Installation
Add the dependency:

pom.xml:
```xml
<dependency>
    <groupId>io.github.mehranmirkhan</groupId>
    <artifactId>cucumber-easy-rest</artifactId>
    <version>0.0.5</version>
    <scope>test</scope>
</dependency>
```

build.gradle:
```gradle
dependencies {
    testImplementation 'io.github.mehranmirkhan:cucumber-easy-rest:0.0.5'
}
```

After adding the dependency, create a test class in your test directory as shown below:

```java
@Suite
@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
@SelectClasspathResource("com/example/demo")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME,
                        value = "io/github/mehranmirkhan/cucumber/rest,com/example/demo")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
class MyApplicationCucumberTests {
}
```

> Note: The `@SelectClasspathResource` annotation specifies the package containing your feature files. Replace it with the appropriate package name for your project. The `@ConfigurationParameter` annotation sets the glue and plugin properties for Cucumber. The glue property should first include the package for the predefined cucumber-easy-rest steps, followed by your custom step definitions.

> Note: This library doesn't prevent you from writing ordinary tests as Java classes. If you find shortcomings in this library that prevent you from expressing a test scenario, simply create a JUnit class and write your test there as usual.

## Usage

This library is intended to be used in `.feature` files. These files use [Gherkin](https://gherkin.org) syntax and are picked up and understood by the [Cucumber](https://cucumber.io/) library.

In the `test/resources` directory of your project, create a directory hierarchy based on your project's package name (and according to the cucumber configuration specified in the previous section). Then add your feature files there. Feature files can be in nested directories as well, so you can organize them as you see fit.

This is an example directory structure for a project with package name `com.example`:

```
src
├── main
└── test
    ├── java
    │   └── com
    │       └── example
    │           └── MyApplicationCucumberTests.java
    └── resources
        └── com
            └── example
                └── signup.feature
```

A feature file will look like this:
```gherkin
Feature: User Signup
  Scenario: Username and Password must be provided
    ...

  Scenario: Password and Confirm password must match
    ...
```

### General Syntax
These are basic step definitions that are not specific to any Spring-related library.

#### Context
The `cucumber-easy-rest` library maintains an internal memory of variables (defined by the user) during test execution. These variables are essential for writing correct tests.

You can define a variable like this:
```gherkin
Given a <- 2  # Variable 'a' is created with value 2
```
With this step, a variable will be created in the internal memory and its value will be set to 2. To update this variable, use the same syntax again:
```gherkin
Given a <- 3  # Variable 'a' is updated to 3
```

These variables can be used throughout the tests with the `&(...)` syntax. For example:
```gherkin
Given b <- &(a)  # Create variable 'b' and set it to the value of variable 'a'
```
This statement will create a new variable `b` and set its value to the value of variable `a`.

> Note: If you write `Given b <- a`, it will create variable b but set its value to the string `"a"`.

To clear the memory, simply write:
```gherkin
Given clear context
```

#### Assertions
The assertion syntax helps validate test results. There are many operations provided for assertions in this library. See the following scenario for usage:
```gherkin
Scenario: Assertions Examples
  Given a <- 3
  Then a = 3
  And a = a
  Given b <- 4
  Then a != b
  And a < b
  And a <= b
  And b > a
  And b >= a
  And LaTeX contains TeX
  And LaTeX contains_ic tEX     # ignore case
  And LaTeX starts_with LaT
  And LaTeX starts_with_ic lAt  # ignore case
  And LaTeX ends_with TeX
  And LaTeX ends_with_ic tEx    # ignore case
  # The following assertions are used with REST steps
  When GET /users/&(userId)
  Then $.name is null
  And $.email is not null
  And $.badges is empty
  And $.roles is not empty
```

> Note: Cucumber doesn't prevent you from using `Given/Then/And/When` keywords interchangeably. So you can write `And a <- 3` or `Given b > 3`, but it is better to use the proper keyword to make the tests more human-readable.

#### Type Processing
An important thing to understand from the previous sections is that all the variables created are strings. When you write `Given a <- 3`, you are creating variable `a` with value `"3"`. Everything is initially interpreted as a string. However, you can explicitly define the type of a value with three type functions: `bool`, `int`, and `double`. Here is an example:
```gherkin
Scenario: Use type
  And 3 = &int(3)
  And 3.0 = &double(3)
```
This looks pointless but becomes crucial once you start using REST or DB steps.
> Note: You can only store strings inside context variables. Even if you write `Given a <- &int(3)`, it will still be stored as a string. This is because variables are substituted using regex pattern matching.

Another important point is that the equality performed here is soft equality; meaning, the left-hand side (LHS) and the right-hand side (RHS) of the equality are compared without considering their type. There is strict equality as well, specified with `==`, which asserts equality if the type matches as well. Strict equality only considers the RHS type. For LHS, the library attempts to automatically discover the type. Therefore, `3 == &int(3)` is correct while `&int(3) == 3` is incorrect. This is because the LHS is supposed to be a variable or a JSON-path (as we will see in REST helpers), and the RHS is supposed to represent the expected value. This difference will make more sense for test implementations.

#### Math
Currently, four math operations are provided: `add` (addition), `sub` (subtraction), `mul` (multiplication), and `div` (division).
```gherkin
Scenario: Math
  Given a <- &add(1,2)  # Variable 'a' will be set to integer 3
  Then a == &int(3)
  And &div(5,2) = 2.5
  And &mul(2,&sub(7,3)) = &add(3,5)
```

#### String Helpers
Currently, two functions are provided for manipulating strings: `lower` and `upper` (to convert to lowercase and uppercase respectively).
```gherkin
Scenario: String Helpers
  Given str1 <- MeHrAn
  Then &lower(&(str1)) = mehran
  Given str2 <- MEHRAN
  Then &(str2) = &upper(&(str1))
```

#### Random Helpers
The following functions are defined to allow the user to generate random numbers and strings:
```gherkin
Scenario: Random Helpers
  Given a <- &rand()    # A random double between 0 and 1
  And b <- &rand(10)    # A random integer between 0 (inclusive) and 10 (exclusive)
  And c <- &rand_a(4)   # A 4-letter random alphabetic string (e.g. aFGb)
  And d <- &rand_n(4)   # A 4-digit random numeric string (e.g. 0374)
  And e <- &rand_an(4)  # A 4-character random alphanumeric string (e.g. F5j0)
```

### DB Helpers
This library provides database manipulation helpers. It is possible to perform full CRUD operations with the database. Let's imagine we have an entity called `Person` with `name` and `age` columns and a unique constraint on the `name` column.
```gherkin
Scenario: DB Helpers
  Given insert records for Person:
    | name | age |
    | Alex | 12  |
    | Lucy | 18  |
  # 2 records will be created in the person table. The IDs of the records will be put into context variables with `id*` names.
  Then alexId <- &(id1)
  And lucyId <- &(id2)
  Given insert records for Person (ignore errors):
    | name | age |
    | Alex | 15  |
  # This insertion will cause an exception (due to unique constraint on the name column) but it will not interrupt the test. The command will do nothing.
  Given upsert records for Person:
    | id        | name | age |
    | &(alexId) | Alex | 15  |
  # Updates the existing record, or creates a new record if it doesn't exist.
  Given update records for Person:
    | id        | name | age |
    | &(alexId) | Alex | 15  |
  # Updates the Alex record
  Given delete records for Person:
    | id        |
    | &(alexId) |
  # Deletes the Alex record
  Given delete records for Person
  # Deletes all records in the person table
  Given find Person:
    | name |
    | lEx  |
  # Finds the Alex record and puts its ID into a variable named `id`.
```
> Note: The `find` helper must be specified in a way that matches only one record in the database. Otherwise, an exception will be thrown.

> As a general convention, when a step definition ends with `:`, it means it expects a table body. This is not a Cucumber/Gherkin restriction, but a convention in the `cucumber-easy-rest` library.

### REST Helpers
If your project has the `spring-web` dependency, you can easily call endpoints in the application using REST step definitions. A few examples:
```gherkin
Scenario: REST Helpers
  When GET /users?name=Alex
  Then status is 200
  And $.content[0].name = Alex
  Given userId <- $.content[0].id
  When GET /users/&(userId)
  Then status is 200
  Then $.name = Alex
  When POST /users:
    | name | Age |
    | Lucy | 18  |
  Then status is 201
```
The `$.content[0].name` expression is a JSON path and the library will try to replace it with the values in that path according to the response of the latest API call (in this case, the latest call was `GET /users?name=Alex`).

The JSON path can be used to perform list assertions like the following:
```gherkin
Scenario: List Assertions
  Given GET /users
  Then status is 200
  And $.content has a match name = Alex
  And $.content has no match age > 15
  And $.content all match id is not null
```
In these cases, the assertion uses `MockMvcResultMatchers.jsonPath`. This means you can write assertions like: `And $.content[?(@.id==1)] exists` (meaning there is an item with id equal to 1) or `And $.content[?(@.id>50)] does not exist` (meaning there is no item with id greater than 50).

The call can also have a request body, which is specified by a datatable after the call expression.

> NOTE: The API call only accepts a request body if the expression ends with `:`.

It is also possible to validate the status code of the response with an expression like `status is xxx`. You can check error statuses as well, like:
```gherkin
  When GET /users/&(userId)
  Then status is 404
  And $.message = User not found
```
> It is good practice to put the status check expression after each API call. This will help you better identify potential issues in your tests because non-200 status codes don't interrupt the test execution and therefore the test might keep going and only fail in later steps.

You can add request headers by providing `-H key=value`:
```gherkin
When GET /users/&(userId) -H Authorization=Bearer 2348mcnhfu94
```

To provide files in the request, simply use `-F[file] data.txt=Hello, World!`. You can provide multiple headers or files. Note that providing even a single `-F` will set the request type to `multipart/form`. The content of the file can be specified in hex byte format with the `0x` prefix: `-F[file1] data.csv=0x1ac3b...`.

The request body accepts JSON objects as well:
```gherkin
Given POST /book:
  | title        | author    |
  | Harry Potter | {"id": 5} |
```
This will result in a request body like the following:
```json
{
  "title": "Harry Potter",
  "author": {
    "id": 5
  }
}
```

### Mocking Spring Beans
This library provides a powerful tool for mocking Spring beans on the fly. To define a mock, you can write:
```gherkin
Scenario: Mock Helper
  Given mock userService.getById(any):
    | id   | name |
    | 1000 | Alex |
```
Now, whenever the `getById` method of the `UserService` bean is invoked, it will return the specified object. If a service doesn't have a return type, you can simply write `Given mock userService.getById(any)` without a table. You can reset mocks by writing `Given reset mocks`.
> Note: At the end of each test scenario, all mocks are cleared automatically. If you want a mock to be maintained in all scenarios of a feature file, simply put the mock in the `Background` section of your feature file:
```gherkin
Feature: Test Users
  Background: Setup mocks
    Given mock userService.getById(any)
  
  Scenario: Check user is found
    When GET /users/5
    Then status is 200
    And $.name = Alex
  
  Scenario: Check user is found again
    When GET /users/6
    Then status is 200
    And $.name = Alex
```
> The `Background` section of a feature file is automatically executed before each test scenario.

### Security Helpers
If your project has the `spring-security` dependency, you can mock the authenticated user in your API calls using the following steps:
```gherkin
Given user:
  | username | roles          |
  | admin    | ["ROLE_ADMIN"] |
# Alternative syntax:
Given user admin with role ROLE_ADMIN
# To just define an admin:
Given admin
# To clear the authentication:
Given anonymous
```
Now, calling the endpoints will be performed with a user with the admin role. Note that for this to work, you must define a class implementing the `UserDetailsProvider` interface. This tells the library which Java class should be used to create the user object.

## Extension
The helper steps defined in this library are comprehensive, yet you might still need something that is not defined here. In such cases, you can simply define your own steps in your test directory. Create an `ExampleStepDef.java` class in the `test/java/...` directory with content like this:
```java
package com.example.demo;

import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

public class ExampleStepDef {
    @Then("^check 1=1$")
    public void myCheck() {
        Assertions.assertEquals(1, 1);
    }
}
```

Now you can use this step in your feature files:
```gherkin
Feature: Some checks
  Scenario: My check
    Then check 1=1
```
