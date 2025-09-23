<a name="top"></a>

![language](https://img.shields.io/badge/language-Java-239120)
![GitHub License](https://img.shields.io/github/license/MehranMirkhan/cucumber-easy-rest)
![GitHub release](https://img.shields.io/github/v/release/MehranMirkhan/cucumber-easy-rest)
![GitHub release date](https://img.shields.io/github/release-date/MehranMirkhan/cucumber-easy-rest)
![GitHub last commit](https://img.shields.io/github/last-commit/MehranMirkhan/cucumber-easy-rest)
![Contributors](https://img.shields.io/github/contributors/MehranMirkhan/cucumber-easy-rest)
![GitHub issues](https://img.shields.io/github/issues/MehranMirkhan/cucumber-easy-rest)
![Github Forks](https://img.shields.io/github/forks/MehranMirkhan/cucumber-easy-rest)
![GitHub Stars](https://img.shields.io/github/stars/MehranMirkhan/cucumber-easy-rest)

⭐ Star this project on GitHub — it motivates us a lot!

[![Share](https://img.shields.io/badge/share-000000?logo=x&logoColor=white)](https://x.com/intent/tweet?text=Check%20out%20this%20project%20on%20GitHub:%20https://github.com/MehranMirkhan/cucumber-easy-rest%20%23OpenIDConnect%20%23Security%20%23Authentication)
[![Share](https://img.shields.io/badge/share-1877F2?logo=facebook&logoColor=white)](https://www.facebook.com/sharer/sharer.php?u=https://github.com/MehranMirkhan/cucumber-easy-rest)
[![Share](https://img.shields.io/badge/share-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/sharing/share-offsite/?url=https://github.com/MehranMirkhan/cucumber-easy-rest)
[![Share](https://img.shields.io/badge/share-FF4500?logo=reddit&logoColor=white)](https://www.reddit.com/submit?title=Check%20out%20this%20project%20on%20GitHub:%20https://github.com/MehranMirkhan/cucumber-easy-rest)
[![Share](https://img.shields.io/badge/share-0088CC?logo=telegram&logoColor=white)](https://t.me/share/url?url=https://github.com/MehranMirkhan/cucumber-easy-rest&text=Check%20out%20this%20project%20on%20GitHub)

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/MehranMirkhan/cucumber-easy-rest">
    <img src="logo.svg" alt="Logo" width="200" height="50">
  </a>

<h3 align="center">Cucumber Easy REST</h3>

  <p align="center">
    An awesome wrapper around cucumber that makes testing REST endpoints so much easier!
    <br />
    <a href="https://github.com/MehranMirkhan/cucumber-easy-rest/wiki"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/MehranMirkhan/cucumber-easy-rest/tree/main/examples">View Examples</a>
    ·
    <a href="https://github.com/MehranMirkhan/cucumber-easy-rest/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    ·
    <a href="https://github.com/MehranMirkhan/cucumber-easy-rest/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>

## Table of Contents
- [About The Project](#-about-the-project)
- [Getting Started](#-getting-started)
- [Usage](#-usage)
- [Contributing](#-contributing)
- [License](#-license)

## About The Project
[Cucumber](https://cucumber.io/) is a tool that enables you to write tests in a human-readable format.
However, it does not provide any built-in semantics for test steps—only pattern matching—leaving the implementation of test logic entirely up to the test writer.

This project addresses that gap by offering a set of predefined steps specifically designed for testing REST endpoints.
With these ready-to-use steps, you can focus on writing meaningful test scenarios without worrying about the underlying implementation details.

## Getting Started
To get started, simply add the following dependency to your project:

pom.xml:
```xml
<dependency>
    <groupId>io.github.mehranmirkhan</groupId>
    <artifactId>cucumber-easy-rest</artifactId>
    <version>0.0.4</version>
    <scope>test</scope>
</dependency>
```

build.gradle:
```gradle
dependencies {
    testImplementation 'io.github.mehranmirkhan:cucumber-easy-rest:0.0.4'
}
```

After adding the dependency, create a test class in your test directory as shown below:

```java
@Suite
@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
@SelectClasspathResource("com/example/full")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME,
                        value = "io/github/mehranmirkhan/cucumber/rest,com/example/full")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
class CucumberEasyRestExampleFullApplicationTest {
}
```

> Note: The `@SelectClasspathResource` annotation specifies the package containing your feature files.
> Replace it with the appropriate package name for your project.
> The `@ConfigurationParameter` annotation sets the glue and plugin properties for Cucumber.
> The glue property should first include the package for the predefined cucumber-easy-rest steps, followed by your custom step definitions.

## Usage
Create feature files in the specified package and write your test scenarios using the predefined steps.

```gherkin
Feature: Users CRUD

  Scenario: Search for a user
    When GET /users?name=John
    Then status is 200
    And $.content[0].name = John
    Given userId <- $.content[0].id
    Given user:
      | username | roles          |
      | admin    | ["ROLE_ADMIN"] |
    Given mock userService.getById(any):
      | id   | firstName |
      | 1000 | Alex      |
    When PUT /users/&(userId):
      | firstName | age      |
      | Jack      | &int(30) |
    Then status is 200
```

See the [examples](https://github.com/MehranMirkhan/cucumber-easy-rest/tree/main/examples)
for more details on writing feature files and using the predefined steps.

> NOTE: The syntax is still evolving and may change in future versions.
> Please open an issue if you have suggestions or feedback.

## Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create.
Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request 

Please make sure to update tests as appropriate.

## License
Distributed under the Apache-2.0 License.
See `LICENSE` for more information.
