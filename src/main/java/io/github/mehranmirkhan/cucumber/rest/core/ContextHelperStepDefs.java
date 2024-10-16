package io.github.mehranmirkhan.cucumber.rest.core;

import io.cucumber.java.en.Given;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContextHelperStepDefs {
    private final HelpersManager helpersManager;
    private final ContextHelper  contextHelper;

    @Given("^Set (\\w[\\w\\d]*) = (.*)$")
    public void setVariable(String key, String value) {
        value = helpersManager.processString(value);
        contextHelper.getVariables().put(key, value);
    }
}
