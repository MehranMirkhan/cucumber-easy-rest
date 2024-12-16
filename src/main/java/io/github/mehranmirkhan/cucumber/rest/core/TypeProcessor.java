package io.github.mehranmirkhan.cucumber.rest.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TypeProcessor {
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^&bool\\((.*)\\)$");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^&int\\((.*)\\)$");
    private static final Pattern DOUBLE_PATTERN  = Pattern.compile("^&double\\((.*)\\)$");

    public Object parseType(String value) {
        if (StringUtils.isBlank(value)) return value;
        Matcher boolMatcher   = BOOLEAN_PATTERN.matcher(value);
        Matcher intMatcher    = INTEGER_PATTERN.matcher(value);
        Matcher doubleMatcher = DOUBLE_PATTERN.matcher(value);
        if (boolMatcher.matches())
            return Boolean.parseBoolean(boolMatcher.group(1));
        else if (intMatcher.matches())
            return Integer.parseInt(intMatcher.group(1));
        else if (doubleMatcher.matches())
            return Double.parseDouble(doubleMatcher.group(1));
        else return value;
    }

    public Object inferType(Object value) {
        if (Objects.isNull(value)) return null;
        if (value instanceof String s) {
            if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s))
                return Boolean.parseBoolean(s);
            else if (NumberUtils.isCreatable(s)) {
                if (s.contains(".")) return Double.parseDouble(s);
                else return Integer.parseInt(s);
            } else return s;
        } else return value;
    }
}
