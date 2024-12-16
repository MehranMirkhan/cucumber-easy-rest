package io.github.mehranmirkhan.cucumber.rest.mvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.core.ContextHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Getter
@RequiredArgsConstructor
public class RestHelperStepDefs {
    public static final String JSON_PATH_REGEX = "(\\$[.|\\[]?\\S*)";

    private final HelpersManager helpersManager;
    private final ObjectMapper   mapper;
    private final MockMvc        mvc;

    private final RestHelper    restHelper;
    private final ContextHelper contextHelper;

    @When("^(GET|POST|PUT|PATCH|DELETE) (\\S+)((?: -H [^=]+=[^=]+)*)(?<!:)$")
    public void mvcRequestWithoutBody(String method, String path, String headers) {
        mvcRequest(method, path, headers, List.of());
    }

    @When("^(GET|POST|PUT|PATCH|DELETE) (\\S+)((?: -H [^=]+=[^=]+)*):$")
    public void mvcRequestWithBody(String method, String path, String headers,
                                   List<Map<String, String>> body) {
        mvcRequest(method, path, headers, body);
    }

    @SneakyThrows
    @Then("status is {int}")
    public void checkStatus(int status) {
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.status().is(status));
    }

    @SneakyThrows
    private void mvcRequest(String method, String path, String headers, List<Map<String, String>> body) {
        path = helpersManager.processString(path);
        List<String> headerList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headers)) {
            headerList = Arrays.stream(headers.split("-H"))
                               .map(String::trim)
                               .filter(StringUtils::isNotEmpty)
                               .map(helpersManager::processString)
                               .toList();
        }
        Map<String, JsonNode> parsedBody = new HashMap<>();
        if (body != null && !body.isEmpty()) {
            for (var entry : body.get(0).entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                v = helpersManager.processString(v);
                if (v != null && (v.startsWith("{") || v.startsWith("[")))
                    parsedBody.put(k, mapper.readTree(v));
                else parsedBody.put(k, TextNode.valueOf(v));
            }
        }
        var req = switch (method) {
            case "GET" -> MockMvcRequestBuilders.get(path);
            case "POST" -> MockMvcRequestBuilders.post(path);
            case "PUT" -> MockMvcRequestBuilders.put(path);
            case "PATCH" -> MockMvcRequestBuilders.patch(path);
            case "DELETE" -> MockMvcRequestBuilders.delete(path);
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
        headerList.forEach(h -> {
            String[] parts = h.split("=");
            req.header(parts[0], parts[1]);
        });
        if (!parsedBody.isEmpty())
            req.content(mapper.writeValueAsString(parsedBody));
        req.contentType(MediaType.APPLICATION_JSON)
           .accept(MediaType.APPLICATION_JSON);
        if (contextHelper.getMockUser() != null)
            req.with(SecurityMockMvcRequestPostProcessors.user(contextHelper.getMockUser()));
        restHelper.setLastResult(mvc.perform(req).andDo(print()));
    }
}
