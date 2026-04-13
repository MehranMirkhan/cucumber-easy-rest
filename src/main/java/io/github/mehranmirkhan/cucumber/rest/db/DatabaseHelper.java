package io.github.mehranmirkhan.cucumber.rest.db;

import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
@Setter
@Order(8)
@Component
@RequiredArgsConstructor
public class DatabaseHelper {
    public static final Pattern JSON_PATTERN = Pattern.compile("(?<!\\$)\\$(\\$(?!\\$)[.\\[]?\\S*)");

    private List<Map<String, Object>> queryResults;

    public boolean hasDbJson(String s) {
        return JSON_PATTERN.matcher(s).matches();
    }

    public Object processResult(String s) {
        var matcher = JSON_PATTERN.matcher(s);
        if (!matcher.matches()) return s;
        String group = matcher.group(1);
        return JsonPath.compile(group).read(getQueryResults());
    }

    public static List<Map<String, Object>> toCamelCase(List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Map<String, Object> converted = new HashMap<>();

            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String camelKey = toCamel(entry.getKey());
                converted.put(camelKey, entry.getValue());
            }

            result.add(converted);
        }

        return result;
    }

    private static String toCamel(String key) {
        key = key.toLowerCase();
        StringBuilder sb = new StringBuilder();
        boolean upper = false;

        for (char c : key.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }

        return sb.toString();
    }
}
