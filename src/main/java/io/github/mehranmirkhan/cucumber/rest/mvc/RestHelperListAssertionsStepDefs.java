package io.github.mehranmirkhan.cucumber.rest.mvc;

import io.cucumber.java.en.Then;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.Utils;
import io.github.mehranmirkhan.cucumber.rest.core.TypeProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.math.NumberUtils;
import org.hamcrest.Matchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Getter
@RequiredArgsConstructor
public class RestHelperListAssertionsStepDefs {
    private final HelpersManager helpersManager;
    private final RestHelper     restHelper;
    private final TypeProcessor  typeProcessor;

    @SneakyThrows
    @Then("^" + RestHelperStepDefs.JSON_PATH_REGEX + " (has a match|has no match|all match) (.*) " +
          "(=|>|<|>=|<=|contains|contains_ic|starts_with|starts_with_ic|ends_with|ends_with_ic) (.*)$")
    public void checkHasAMatch(String jsonPath, String checkType, String key, String op, String value) {
        value = helpersManager.processString(value);
        Object value2 = typeProcessor.parseType(value);
        if (value2 instanceof String s && NumberUtils.isCreatable(s))
            value2 = Utils.parseIntOrDouble(s);
        var matcher = switch (op) {
            case "=" -> Matchers.is(value2);
            case ">" -> Matchers.greaterThan((Comparable) value2);
            case ">=" -> Matchers.greaterThanOrEqualTo((Comparable) value2);
            case "<" -> Matchers.lessThan((Comparable) value2);
            case "<=" -> Matchers.lessThanOrEqualTo((Comparable) value2);
            case "contains" -> Matchers.containsString(value);
            case "contains_ic" -> Matchers.containsStringIgnoringCase(value);
            case "starts_with" -> Matchers.startsWith(value);
            case "starts_with_ic" -> Matchers.startsWithIgnoringCase(value);
            case "ends_with" -> Matchers.endsWith(value);
            case "ends_with_ic" -> Matchers.endsWithIgnoringCase(value);
            default -> throw new IllegalArgumentException("Illegal operator");
        };
        switch (checkType) {
            case "has a match" -> restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                    jsonPath, Matchers.hasItem(Matchers.hasEntry(Matchers.is(key), matcher))));
            case "has no match" -> restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                    jsonPath, Matchers.not(Matchers.hasItem(Matchers.hasEntry(Matchers.is(key), matcher)))));
            case "all match" -> restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                    jsonPath, Matchers.everyItem(Matchers.hasEntry(Matchers.is(key), matcher))));
            default -> throw new IllegalArgumentException("Illegal check method");
        }
    }
}
