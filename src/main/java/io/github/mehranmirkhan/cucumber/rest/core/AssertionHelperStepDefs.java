package io.github.mehranmirkhan.cucumber.rest.core;

import io.cucumber.java.en.Then;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.mvc.RestHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RequiredArgsConstructor
public class AssertionHelperStepDefs {
    private static final String LHS_PATTERN = "(\\S+)";
    private static final String RHS_PATTERN = "(.+)";

    private final HelpersManager helpersManager;
    private final TypeProcessor  typeProcessor;
    private final RestHelper     restHelper;

    public enum Operator {
        EQUAL, SOFT_EQUAL, NOT_EQUAL,
        GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL,
        HAS_SIZE,
        CONTAINS, CONTAINS_IC, STARTS_WITH, STARTS_WITH_IC, ENDS_WITH, ENDS_WITH_IC,
        IS_EMPTY, IS_NOT_EMPTY, IS_NULL, IS_NOT_NULL, EXISTS, NOT_EXISTS,
    }

    public enum ListMatch {
        HAS_A_MATCH, HAS_NO_MATCH, ALL_MATCH;

        static final String PATTERN = "(has a match|has no match|all match)";

        static ListMatch of(String name) {
            if (name == null) return null;
            return switch (name) {
                case "has a match" -> ListMatch.HAS_A_MATCH;
                case "has no match" -> ListMatch.HAS_NO_MATCH;
                case "all match" -> ListMatch.ALL_MATCH;
                default -> null;
            };
        }
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " = " + RHS_PATTERN + "$")
    public void assertEq(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.SOFT_EQUAL, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " == " + RHS_PATTERN + "$")
    public void assertEqExact(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.EQUAL, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " != " + RHS_PATTERN + "$")
    public void assertNotEq(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.NOT_EQUAL, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " > " + RHS_PATTERN + "$")
    public void assertGt(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.GREATER_THAN, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " >= " + RHS_PATTERN + "$")
    public void assertGe(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.GREATER_THAN_OR_EQUAL, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " < " + RHS_PATTERN + "$")
    public void assertLt(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.LESS_THAN, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " <= " + RHS_PATTERN + "$")
    public void assertLe(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.LESS_THAN_OR_EQUAL, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " has_size " + RHS_PATTERN + "$")
    public void assertSize(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.HAS_SIZE, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " contains " + RHS_PATTERN + "$")
    public void assertContains(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.CONTAINS, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " contains_ic " + RHS_PATTERN + "$")
    public void assertContainsIgnoreCase(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.CONTAINS_IC, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " starts_with " + RHS_PATTERN + "$")
    public void assertStartsWith(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.STARTS_WITH, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " starts_with_ic " + RHS_PATTERN + "$")
    public void assertStartsWithIgnoreCase(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.STARTS_WITH_IC, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " ends_with " + RHS_PATTERN + "$")
    public void assertEndsWith(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.ENDS_WITH, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " ends_with_ic " + RHS_PATTERN + "$")
    public void assertEndsWithIgnoreCase(String lhs, String listMatch, String listKey, String rhs) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.ENDS_WITH_IC, rhs);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " is empty$")
    public void assertEmpty(String lhs, String listMatch, String listKey) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.IS_EMPTY, null);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " is not empty$")
    public void assertNotEmpty(String lhs, String listMatch, String listKey) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.IS_NOT_EMPTY, null);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " is null")
    public void assertNull(String lhs, String listMatch, String listKey) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.IS_NULL, null);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " is not null")
    public void assertNotNull(String lhs, String listMatch, String listKey) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.IS_NOT_NULL, null);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " exists$")
    public void assertExists(String lhs, String listMatch, String listKey) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.EXISTS, null);
    }

    @Then("^" + LHS_PATTERN + "\\s?" + ListMatch.PATTERN + "?\\s?" + LHS_PATTERN + "?" + " does not exist$")
    public void assertDoesNotExist(String lhs, String listMatch, String listKey) {
        check(lhs, ListMatch.of(listMatch), listKey, Operator.NOT_EXISTS, null);
    }

    @SneakyThrows
    protected void check(String lhs, Operator op, String rhs) {
        lhs = helpersManager.processString(lhs);
        rhs = helpersManager.processString(rhs);
        Object lhsObj          = typeProcessor.parseType(lhs);
        Object rhsObj          = typeProcessor.parseType(rhs);
        Object lhsInferredType = typeProcessor.inferType(lhsObj);
        Object rhsInferredType = typeProcessor.inferType(rhsObj);
        var    matcher         = getMatcherForOperator(op, rhs, rhsInferredType, rhsObj);
        if (RestHelper.JSON_PATTERN.matcher(lhs).matches()) {
            switch (op) {
                case EXISTS -> restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(lhs).exists());
                case NOT_EXISTS ->
                        restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(lhs).doesNotExist());
                default -> restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(lhs, matcher));
            }
        } else
            MatcherAssert.assertThat(lhsInferredType, matcher);
    }

    @SneakyThrows
    protected void check(String lhs, ListMatch listMatch, String listKey, Operator op, String rhs) {
        if (listMatch == null) {
            check(lhs, op, rhs);
            return;
        }
        lhs = helpersManager.processString(lhs);
        rhs = helpersManager.processString(rhs);
        listKey = listKey.trim();
        Object rhsObj          = typeProcessor.parseType(rhs);
        Object rhsInferredType = typeProcessor.inferType(rhsObj);
        var    matcher         = getMatcherForOperator(op, rhs, rhsInferredType, rhsObj);
        switch (listMatch) {
            case HAS_A_MATCH -> restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                    lhs, Matchers.hasItem(Matchers.hasEntry(Matchers.is(listKey), matcher))));
            case HAS_NO_MATCH -> restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                    lhs, Matchers.not(Matchers.hasItem(Matchers.hasEntry(Matchers.is(listKey), matcher)))));
            case ALL_MATCH -> restHelper.getLastResult().andExpect(MockMvcResultMatchers.jsonPath(
                    lhs, Matchers.everyItem(Matchers.hasEntry(Matchers.is(listKey), matcher))));
        }
    }

    private static Matcher getMatcherForOperator(Operator op,
                                                 String rhs,
                                                 Object rhsInferredType,
                                                 Object rhsObj) {
        return switch (op) {
            case SOFT_EQUAL -> Matchers.either(Matchers.is(rhsInferredType)).or(Matchers.is(rhsObj));
            case EQUAL -> Matchers.is(rhsObj);
            case NOT_EQUAL -> Matchers.not(Matchers.is(rhsObj));
            case GREATER_THAN -> Matchers.greaterThan((Comparable) rhsInferredType);
            case GREATER_THAN_OR_EQUAL -> Matchers.greaterThanOrEqualTo((Comparable) rhsInferredType);
            case LESS_THAN -> Matchers.lessThan((Comparable) rhsInferredType);
            case LESS_THAN_OR_EQUAL -> Matchers.lessThanOrEqualTo((Comparable) rhsInferredType);
            case CONTAINS -> Matchers.containsString(rhs);
            case CONTAINS_IC -> Matchers.containsStringIgnoringCase(rhs);
            case STARTS_WITH -> Matchers.startsWith(rhs);
            case STARTS_WITH_IC -> Matchers.startsWithIgnoringCase(rhs);
            case ENDS_WITH -> Matchers.endsWith(rhs);
            case ENDS_WITH_IC -> Matchers.endsWithIgnoringCase(rhs);
            case HAS_SIZE -> Matchers.hasSize((Integer) rhsInferredType);
            case IS_EMPTY -> Matchers.empty();
            case IS_NOT_EMPTY -> Matchers.not(Matchers.empty());
            case IS_NULL -> Matchers.nullValue();
            case IS_NOT_NULL -> Matchers.not(Matchers.nullValue());
            case EXISTS -> Matchers.notNullValue();
            case NOT_EXISTS -> Matchers.nullValue();
        };
    }
}
