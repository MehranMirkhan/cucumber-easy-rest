package io.github.mehranmirkhan.cucumber.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.github.mehranmirkhan.cucumber.rest.core.ContextHelper;
import io.github.mehranmirkhan.cucumber.rest.core.MathHelper;
import io.github.mehranmirkhan.cucumber.rest.core.RandHelper;
import io.github.mehranmirkhan.cucumber.rest.core.StringHelper;
import io.github.mehranmirkhan.cucumber.rest.core.TypeProcessor;
import io.github.mehranmirkhan.cucumber.rest.db.DatabaseHelper;
import io.github.mehranmirkhan.cucumber.rest.mvc.RestHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Import({HelpersManager.class, ContextHelper.class, MathHelper.class, RandHelper.class,
        StringHelper.class, DatabaseHelper.class, RestHelper.class, TypeProcessor.class,})
public class CucumberEasyRestAutoConfiguration {
    public CucumberEasyRestAutoConfiguration() {
        log.info("Cucumber REST is loaded.");
    }

    public static void main(String[] args) {}
}
