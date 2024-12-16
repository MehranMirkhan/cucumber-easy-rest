package io.github.mehranmirkhan.cucumber.rest.core;

import io.cucumber.java.en.Then;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

@RequiredArgsConstructor
public class AssertionHelperStepDefs {
    private final HelpersManager helpersManager;

    @Then("^([^\\$\\s]+) = (\\S+)$")
    public void assertEq(String lhs, String rhs) {
        lhs = helpersManager.processString(lhs);
        rhs = helpersManager.processString(rhs);
        Assertions.assertEquals(rhs, lhs);
    }

    @Then("^([^\\$\\s]+) != (\\S+)$")
    public void assertNotEq(String lhs, String rhs) {
        lhs = helpersManager.processString(lhs);
        rhs = helpersManager.processString(rhs);
        Assertions.assertNotEquals(rhs, lhs);
    }

    @Then("^([^\\$\\s]+) > (\\S+)$")
    public void assertGt(String lhs, String rhs) {
        lhs = helpersManager.processString(lhs);
        rhs = helpersManager.processString(rhs);
        Assertions.assertTrue(Double.parseDouble(lhs) > Double.parseDouble(rhs));
    }

    @Then("^([^\\$\\s]+) >= (\\S+)$")
    public void assertGe(String lhs, String rhs) {
        lhs = helpersManager.processString(lhs);
        rhs = helpersManager.processString(rhs);
        Assertions.assertTrue(Double.parseDouble(lhs) >= Double.parseDouble(rhs));
    }

    @Then("^([^\\$\\s]+) < (\\S+)$")
    public void assertLt(String lhs, String rhs) {
        lhs = helpersManager.processString(lhs);
        rhs = helpersManager.processString(rhs);
        Assertions.assertTrue(Double.parseDouble(lhs) < Double.parseDouble(rhs));
    }

    @Then("^([^\\$\\s]+) <= (\\S+)$")
    public void assertLe(String lhs, String rhs) {
        lhs = helpersManager.processString(lhs);
        rhs = helpersManager.processString(rhs);
        Assertions.assertTrue(Double.parseDouble(lhs) <= Double.parseDouble(rhs));
    }

    @Then("^([^\\$\\s]+) is null$")
    public void assertNull(String v) {
        v = helpersManager.processString(v);
        Assertions.assertNull(v);
    }

    @Then("^([^\\$\\s]+) is not null$")
    public void assertNotNull(String v) {
        v = helpersManager.processString(v);
        Assertions.assertNotNull(v);
    }

    @Then("^([^\\$\\s]+) is empty")
    public void assertEmpty(String v) {
        v = helpersManager.processString(v);
        Assertions.assertTrue(StringUtils.isEmpty(v));
    }

    @Then("^([^\\$\\s]+) is not empty")
    public void assertNotEmpty(String v) {
        v = helpersManager.processString(v);
        Assertions.assertTrue(StringUtils.isNotEmpty(v));
    }
}
