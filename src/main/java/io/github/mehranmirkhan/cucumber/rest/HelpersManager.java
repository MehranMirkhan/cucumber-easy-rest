package io.github.mehranmirkhan.cucumber.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mehranmirkhan.cucumber.rest.core.TypeProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HelpersManager {
    private final List<CucumberHelper> helpers;
    private final ObjectMapper         mapper;
    private final TypeProcessor        typeProcessor;

    public String processString(String s) {
        if (StringUtils.isBlank(s)) return s;
        List<CucumberHelper> safeHelpers = helpers != null ? helpers : Collections.emptyList();

        String result = s;
        for (CucumberHelper helper : safeHelpers) {
            if (StringUtils.isEmpty(result)) break;
            result = helper.processString(result.trim());
        }
        return result;
    }

    public Object parseJson(String s) throws JsonProcessingException {
        String processed = processString(s);
        if (StringUtils.isNotEmpty(processed) && (processed.startsWith("{") || processed.startsWith("["))) {
            return mapper.readTree(processed);
        }
        return typeProcessor.parseType(processed);
    }

    public Map<String, Object> processMap(Map<String, String> body) {
        if (body == null || body.isEmpty())
            return Collections.emptyMap();
        Map<String, String> updatedEntry = new HashMap<>();
        body.forEach((k, v) -> updatedEntry.put(k, processString(v)));
        Map<String, Object> updatedNestedEntry = new HashMap<>();
        updatedEntry.forEach((k, v) -> {
            Object value;
            try {
                value = parseJson(v);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse JSON for key: " + k, e);
            }
            String[]            keyParts = k.split("\\.");
            Map<String, Object> current  = updatedNestedEntry;
            for (int i = 0; i < keyParts.length - 1; i++) {
                current = (Map<String, Object>) current.computeIfAbsent(keyParts[i], kk -> new HashMap<>());
            }
            current.put(keyParts[keyParts.length - 1], value);
        });
        return updatedNestedEntry;
    }

    public List<Map<String, Object>> processTable(List<Map<String, String>> body) {
        if (body == null || body.isEmpty())
            return Collections.emptyList();
        return body.stream().map(this::processMap).toList();
    }

    public <T> List<T> parseBody(List<Map<String, String>> body, Class<T> _class) {
        if (body == null || body.isEmpty())
            return Collections.emptyList();
        return processTable(body).stream()
                                 .map(entry -> mapper.convertValue(entry, _class))
                                 .toList();
    }
}
