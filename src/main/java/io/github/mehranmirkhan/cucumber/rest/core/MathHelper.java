package io.github.mehranmirkhan.cucumber.rest.core;

import io.github.mehranmirkhan.cucumber.rest.CucumberHelper;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

@Order(12)
@Component
public class MathHelper implements CucumberHelper {
    protected static final String  NUMBER_REGEX = "[+-]?[0-9]*[.]?[0-9]+";
    protected static final Pattern ADD_PATTERN  = Pattern.compile("&add\\((%s),(%s)\\)"
                                                                          .formatted(NUMBER_REGEX, NUMBER_REGEX));
    protected static final Pattern SUB_PATTERN  = Pattern.compile("&sub\\((%s),(%s)\\)"
                                                                          .formatted(NUMBER_REGEX, NUMBER_REGEX));
    protected static final Pattern MUL_PATTERN  = Pattern.compile("&mul\\((%s),(%s)\\)"
                                                                          .formatted(NUMBER_REGEX, NUMBER_REGEX));
    protected static final Pattern DIV_PATTERN  = Pattern.compile("&div\\((%s),(%s)\\)"
                                                                          .formatted(NUMBER_REGEX, NUMBER_REGEX));

    protected enum F {ADD, SUB, MUL, DIV}

    @Override
    public String processString(String s) {
        String prevS;
        do {
            prevS = s;
            s = ADD_PATTERN.matcher(s).replaceAll(r -> calculate(F.ADD, r.group(1), r.group(2)));
            s = SUB_PATTERN.matcher(s).replaceAll(r -> calculate(F.SUB, r.group(1), r.group(2)));
            s = MUL_PATTERN.matcher(s).replaceAll(r -> calculate(F.MUL, r.group(1), r.group(2)));
            s = DIV_PATTERN.matcher(s).replaceAll(r -> calculate(F.DIV, r.group(1), r.group(2)));
        } while (!Objects.equals(s, prevS));
        return s;
    }

    protected String calculate(F f, String arg1, String arg2) {
        if (!NumberUtils.isCreatable(arg1) || !NumberUtils.isCreatable(arg2))
            throw new IllegalArgumentException("MathHelper cannot process arguments: %s(%s,%s)"
                                                       .formatted(f.name(), arg1, arg2));
        Number num1 = arg1.contains(".") ? (Number) NumberUtils.createDouble(arg1) :
                      (Number) NumberUtils.createInteger(arg1);
        Number num2 = arg2.contains(".") ? (Number) NumberUtils.createDouble(arg2) :
                      (Number) NumberUtils.createInteger(arg2);
        return switch (f) {
            case ADD -> add(num1, num2);
            case SUB -> sub(num1, num2);
            case MUL -> mul(num1, num2);
            case DIV -> div(num1, num2);
        };
    }

    protected String add(Number a, Number b) {
        Number result;
        if (a instanceof Double || b instanceof Double)
            result = a.doubleValue() + b.doubleValue();
        else
            result = a.intValue() + b.intValue();
        return result.toString();
    }

    protected String sub(Number a, Number b) {
        Number result;
        if (a instanceof Double || b instanceof Double)
            result = a.doubleValue() - b.doubleValue();
        else
            result = a.intValue() - b.intValue();
        return result.toString();
    }

    protected String mul(Number a, Number b) {
        Number result;
        if (a instanceof Double || b instanceof Double)
            result = a.doubleValue() * b.doubleValue();
        else
            result = a.intValue() * b.intValue();
        return result.toString();
    }

    protected String div(Number a, Number b) {
        Number result;
        if (a instanceof Double || b instanceof Double)
            result = a.doubleValue() / b.doubleValue();
        else
            result = a.intValue() / b.intValue();
        return result.toString();
    }
}
