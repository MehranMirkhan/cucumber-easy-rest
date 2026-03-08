package com.example.full;

import io.cucumber.junit.platform.engine.Constants;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

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