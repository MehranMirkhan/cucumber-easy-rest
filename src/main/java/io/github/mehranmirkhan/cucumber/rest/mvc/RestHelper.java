package io.github.mehranmirkhan.cucumber.rest.mvc;

import com.jayway.jsonpath.JsonPath;
import io.github.mehranmirkhan.cucumber.rest.CucumberHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Order(7)
@Component
@RequiredArgsConstructor
public class RestHelper implements CucumberHelper {
    private static final Pattern JSON_PATTERN = Pattern.compile("(\\$[.\\[]?\\S*)");

    private ResultActions lastResult;

    public String getLastResponse() {
        return Optional.ofNullable(lastResult)
                       .map(ResultActions::andReturn)
                       .map(MvcResult::getResponse)
                       .map(res -> {
                           try {
                               return res.getContentAsString();
                           } catch (UnsupportedEncodingException e) {
                               throw new RuntimeException(e);
                           }
                       })
                       .orElse(null);
    }

    @Override
    public String processString(String s) {
        Matcher matcher = JSON_PATTERN.matcher(s);
        if (!matcher.matches()) return s;
        String group = matcher.group();
        s = Optional.ofNullable(JsonPath.compile(group).read(getLastResponse()))
                    .map(Object::toString)
                    .orElse(null);
        return s;
    }
}
