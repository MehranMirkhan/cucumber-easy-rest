package com.example.full;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = Params.class)
public class CucumberEasyRestExampleFullApplication {
    public static void main(String[] args) {
        SpringApplication.run(CucumberEasyRestExampleFullApplication.class, args);
    }
}
