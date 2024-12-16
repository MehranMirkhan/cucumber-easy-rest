package io.github.mehranmirkhan.cucumber.rest.core;

import io.cucumber.java.en.Given;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.mvc.RestHelper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContextHelperStepDefs {
    private final HelpersManager helpersManager;
    private final ContextHelper  contextHelper;
    private final RestHelper restHelper;

    @Given("^(\\w[\\w\\d]*) <- (.*)$")
    public void setVariable(String key, String value) {
        value = helpersManager.processString(value);
        Object valueObj = restHelper.processResponse(value);
        contextHelper.getVariables().put(key, valueObj.toString());
    }
}
