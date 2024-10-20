package com.example.core;

import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

public class ExampleStepDef {
    @Then("^correct$")
    public void correct() {
        Assertions.assertEquals(1, 1);
    }
}
