package io.github.mehranmirkhan.cucumber.rest.core;

import io.github.mehranmirkhan.cucumber.rest.CucumberHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.regex.Pattern;

@Order(10)
@Component
public class RandHelper implements CucumberHelper {
    private static final Random random = new Random();

    private static final Pattern RAND_FLOAT_PATTERN         = Pattern.compile("&rand\\(\\)");
    private static final Pattern RAND_INT_PATTERN           = Pattern.compile("&rand\\((\\d+)\\)");
    private static final Pattern RAND_ALPHA_PATTERN         = Pattern.compile("&rand_a\\((\\d+)\\)");
    private static final Pattern RAND_NUMERIC_PATTERN       = Pattern.compile("&rand_n\\((\\d+)\\)");
    private static final Pattern RAND_ALPHA_NUMERIC_PATTERN = Pattern.compile("&rand_an\\((\\d+)\\)");

    private final RandomStringUtils insecure = RandomStringUtils.insecure();

    @Override
    public String processString(String s) {
        s = RAND_FLOAT_PATTERN.matcher(s).replaceAll(r -> String.valueOf(random.nextFloat()));
        s = RAND_INT_PATTERN.matcher(s).replaceAll(r -> String.valueOf(random.nextInt(Integer.parseInt(r.group(1)))));
        s = RAND_ALPHA_PATTERN.matcher(s).replaceAll(r -> insecure.nextAlphabetic(Integer.parseInt(r.group(1))));
        s = RAND_NUMERIC_PATTERN.matcher(s).replaceAll(r -> insecure.nextNumeric(Integer.parseInt(r.group(1))));
        s = RAND_ALPHA_NUMERIC_PATTERN.matcher(s).replaceAll(r -> insecure.nextAlphanumeric(Integer.parseInt(r.group(1))));
        return s;
    }
}
