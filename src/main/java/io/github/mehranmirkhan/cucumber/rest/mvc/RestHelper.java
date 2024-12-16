package io.github.mehranmirkhan.cucumber.rest.mvc;

import com.jayway.jsonpath.JsonPath;
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
public class RestHelper {
    public static final Pattern JSON_PATTERN = Pattern.compile("(\\$[.\\[]?\\S*)");

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

    public Object processResponse(String s) {
        Matcher matcher = JSON_PATTERN.matcher(s);
        if (!matcher.matches()) return s;
        String group = matcher.group();
        return JsonPath.compile(group).read(getLastResponse());
    }
}
