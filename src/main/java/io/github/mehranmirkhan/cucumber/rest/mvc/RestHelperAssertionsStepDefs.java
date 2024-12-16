package io.github.mehranmirkhan.cucumber.rest.mvc;

import io.cucumber.java.en.Then;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.Utils;
import io.github.mehranmirkhan.cucumber.rest.core.TypeProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hamcrest.Matchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Getter
@RequiredArgsConstructor
public class RestHelperAssertionsStepDefs {
    private final HelpersManager helpersManager;
    private final RestHelper     restHelper;
    private final TypeProcessor  typeProcessor;

    @SneakyThrows
    @Then("status is {int}")
    public void checkStatus(int status) {
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.status().is(status));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " is empty$")
    public void checkIsEmpty(String jsonPath) {
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath).isEmpty());
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " is not empty$")
    public void checkIsNotEmpty(String jsonPath) {
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath).isNotEmpty());
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " exists$")
    public void checkExists(String jsonPath) {
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath).exists());
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " does not exist$")
    public void checkDoesNotExists(String jsonPath) {
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath).doesNotExist());
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " is null")
    public void checkIsNull(String jsonPath) {
        restHelper.getLastResult().andExpect(
                MockMvcResultMatchers.jsonPath(jsonPath).value(Matchers.nullValue()));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " is not null")
    public void checkIsNotNull(String jsonPath) {
        restHelper.getLastResult().andExpect(
                MockMvcResultMatchers.jsonPath(jsonPath).value(Matchers.notNullValue()));
    }

//    @SneakyThrows
//    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " " +
//          "(=|>|<|>=|<=|contains|contains_ic|starts_with|starts_with_ic|ends_with|ends_with_ic) (.*)$")
//    public void check(String jsonPath, String op, String value) {
//        value = helpersManager.processString(value);
//        Object value2 = typeProcessor.parseType(value);
//        if (value2 instanceof String s && NumberUtils.isCreatable(s))
//            value2 = Utils.parseIntOrDouble(s);
//        var matcher = switch (op) {
//            case "=" -> Matchers.is(value2);
//            case ">" -> Matchers.greaterThan((Comparable) value2);
//            case ">=" -> Matchers.greaterThanOrEqualTo((Comparable) value2);
//            case "<" -> Matchers.lessThan((Comparable) value2);
//            case "<=" -> Matchers.lessThanOrEqualTo((Comparable) value2);
//            case "contains" -> Matchers.containsString(value);
//            case "contains_ic" -> Matchers.containsStringIgnoringCase(value);
//            case "starts_with" -> Matchers.startsWith(value);
//            case "starts_with_ic" -> Matchers.startsWithIgnoringCase(value);
//            case "ends_with" -> Matchers.endsWith(value);
//            case "ends_with_ic" -> Matchers.endsWithIgnoringCase(value);
//            default -> throw new IllegalArgumentException("Illegal operator");
//        };
//        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath, matcher));
//    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " = (.*)$")
    public void checkIs(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath).value(value));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " != (.*)$")
    public void checkIsNot(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath)
                                                                  .value(Matchers.not(value)));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " > (.*)$")
    public void checkGreater(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                jsonPath, Matchers.greaterThan(Utils.parseIntOrDouble(value))));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " >= (.*)$")
    public void checkGreaterOrEqual(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                jsonPath, Matchers.greaterThanOrEqualTo(Utils.parseIntOrDouble(value))));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " < (.*)$")
    public void checkLess(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                jsonPath, Matchers.lessThan(Utils.parseIntOrDouble(value))));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " <= (.*)$")
    public void checkLessOrEqual(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                jsonPath, Matchers.lessThanOrEqualTo(Utils.parseIntOrDouble(value))));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " starts_with (.*)$")
    public void checkStartsWith(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath)
                                                                  .value(Matchers.startsWith(value)));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " starts_with_ic (.*)$")
    public void checkStartsWithIgnoreCase(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath)
                                                                  .value(Matchers.startsWithIgnoringCase(value)));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " ends_with (.*)$")
    public void checkEndsWith(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath)
                                                                  .value(Matchers.endsWith(value)));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " ends_with_ic (.*)$")
    public void checkEndsWithIgnoreCase(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath)
                                                                  .value(Matchers.endsWithIgnoringCase(value)));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " contains (.*)$")
    public void checkContains(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath)
                                                                  .value(Matchers.containsString(value)));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " contains_ic (.*)$")
    public void checkContainsIgnoreCase(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(jsonPath)
                                                                  .value(Matchers.containsStringIgnoringCase(value)));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " has size (.*)$")
    public void checkHasSize(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(
                MockMvcResultMatchers.jsonPath(jsonPath, Matchers.hasSize(Integer.parseInt(value))));
    }

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " matches (.*)$")
    public void checkRegexMatch(String jsonPath, String value) {
        value = helpersManager.processString(value);
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                jsonPath, Matchers.matchesRegex(value)));
    }
}
