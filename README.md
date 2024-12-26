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
The Cucumber is a tool that only provides a way to write tests in a human-readable format.
It does not provide any specific semantic about the test steps. There is only pattern matching,
and it is up to the test writer to implement the actual test logic.

This project aims to solve this problem by providing a set of predefined steps that can be used
to test REST endpoints. This way, the test writer can focus on writing the actual test scenarios
and not worry about the implementation details.

## Getting Started
Simply add the following dependency to your project.

pom.xml:
```xml
<dependency>
    <groupId>com.github.mehranmirkhan</groupId>
    <artifactId>cucumber-easy-rest</artifactId>
    <version>0.0.3</version>
    <scope>test</scope>
</dependency>
```

build.gradle:
```gradle
dependencies {
    testImplementation 'com.github.mehranmirkhan:cucumber-easy-rest:0.0.3'
}
```

Once the dependency is added, Create a test class in your test directory like this:

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

> Note: The `@SelectClasspathResource` annotation is used to specify the package where the feature files are located.
> Please replace it with the actual package name in your project.
> The `@ConfigurationParameter` annotation is used to specify the glue and plugin properties for cucumber.
> It specifies where the step definitions are located. You should first specify the package where the predefined
> cucumber-easy-rest steps are, followed by the package of your custom steps.

## Usage
You can create feature files in the specified package and write your test scenarios.

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
    When PUT /users/&(userId):
      | firstName |
      | Jack      |
    Then status is 200
```

Please see [examples](https://github.com/MehranMirkhan/cucumber-easy-rest/tree/main/examples)
For more details on how to write feature files and use the predefined steps.

> NOTE: The syntax is still not mature enough and may change in the future versions.
> Please create an issue if you have any suggestions or feedback.

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
