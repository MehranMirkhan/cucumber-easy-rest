package io.github.mehranmirkhan.cucumber.rest.core;

import io.github.mehranmirkhan.cucumber.rest.CucumberHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
@Setter
@Order(5)
@Component
public class ContextHelper implements CucumberHelper {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("&\\(([^()]+)\\)");

    private final Map<String, String> variables = new HashMap<>();

    private UserDetails mockUser = null;

    @Override
    public String processString(String s) {
        return VARIABLE_PATTERN.matcher(s).replaceAll(r -> variables.get(r.group(1)));
    }
}
