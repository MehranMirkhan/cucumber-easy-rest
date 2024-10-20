package com.example.core;

import io.cucumber.junit.platform.engine.Constants;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SelectClasspathResource("com/example/core")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME,
                        value = "io/github/mehranmirkhan/cucumber/rest,com/example/core")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@SpringBootTest
@CucumberContextConfiguration
class CucumberRestSpringExampleCoreApplicationTests {
}