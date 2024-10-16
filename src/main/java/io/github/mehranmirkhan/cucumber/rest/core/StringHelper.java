package io.github.mehranmirkhan.cucumber.rest.core;

import io.github.mehranmirkhan.cucumber.rest.CucumberHelper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Order(15)
@Component
public class StringHelper implements CucumberHelper {
    private static final Pattern LOWER_PATTERN = Pattern.compile("&lower\\(([^()]+)\\)");
    private static final Pattern UPPER_PATTERN = Pattern.compile("&upper\\(([^()]+)\\)");

    @Override
    public String processString(String s) {
        s = LOWER_PATTERN.matcher(s).replaceAll(r -> r.group(1).toLowerCase());
        s = UPPER_PATTERN.matcher(s).replaceAll(r -> r.group(1).toUpperCase());
        return s;
    }
}
