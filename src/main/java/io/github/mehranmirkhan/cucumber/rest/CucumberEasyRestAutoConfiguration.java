package io.github.mehranmirkhan.cucumber.rest;

import io.github.mehranmirkhan.cucumber.rest.core.*;
import io.github.mehranmirkhan.cucumber.rest.mvc.RestHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@Import({
        HelpersManager.class,
        ContextHelper.class,
        RandHelper.class,
        MathHelper.class,
        StringHelper.class,
        RestHelper.class,
        TypeProcessor.class,
})
public class CucumberEasyRestAutoConfiguration {
    public CucumberEasyRestAutoConfiguration() {
        log.info("Cucumber REST is loaded.");
    }

    public static void main(String[] args) {
    }
}
