package io.github.mehranmirkhan.cucumber.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HelpersManager {
    private final List<CucumberHelper> helpers;
    private final ObjectMapper         mapper;

    public String processString(String s) {
        return Optional.ofNullable(helpers)
                       .orElse(List.of())
                       .stream()
                       .reduce(s,
                               (str, helper) -> StringUtils.isEmpty(str) ? str : helper.processString(str.trim()),
                               (str1, str2) -> str2);
    }

    public JsonNode parseJson(String s) throws JsonProcessingException {
        s = processString(s);
        if (StringUtils.isNotEmpty(s) && (s.startsWith("{") || s.startsWith("[")))
            return mapper.readTree(s);
        else return TextNode.valueOf(s);
    }

    public <T> List<T> parseBody(List<Map<String, String>> body, Class<T> _class) {
        return body.stream()
                   .map(entry -> {
                       Map<String, String> updatedEntry = new HashMap<>();
                       entry.forEach((k, v) -> updatedEntry.put(k, processString(v)));
                       Map<String, Object> updatedNestedEntry = new HashMap<>();
                       updatedEntry.forEach((k, v) -> {
                           String k2 = k;
                           Object v2 = v;
                           if (v.startsWith("{") || v.startsWith("[")) {
                               try {
                                   v2 = mapper.readTree(v);
                               } catch (JsonProcessingException e) {
                                   throw new RuntimeException(e);
                               }
                           }
                           if (k.contains(".")) {
                               String[] keyParts = k2.split("\\.");
                               k2 = keyParts[0];
                               v2 = Map.of(keyParts[1], v2);
                           }
                           updatedNestedEntry.put(k2, v2);
                       });
                       return mapper.convertValue(updatedNestedEntry, _class);
                   }).toList();
    }
}
