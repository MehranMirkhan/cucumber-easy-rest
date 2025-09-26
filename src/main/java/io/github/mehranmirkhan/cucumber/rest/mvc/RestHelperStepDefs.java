package io.github.mehranmirkhan.cucumber.rest.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.core.ContextHelper;
import io.github.mehranmirkhan.cucumber.rest.core.TypeProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Getter
@RequiredArgsConstructor
public class RestHelperStepDefs {
    public static final String  JSON_PATH_REGEX = "(\\$[.|\\[]?\\S*)";
    public static final String  FILE_REGEX      = "\\[(.+)] ([^=]+)=([^=]+)";
    public static final Pattern FILE_PATTERN    = Pattern.compile(FILE_REGEX);

    private final HelpersManager helpersManager;
    private final ObjectMapper   mapper;
    private final MockMvc        mvc;

    private final RestHelper    restHelper;
    private final ContextHelper contextHelper;
    private final TypeProcessor typeProcessor;

    @When("^(GET|POST|PUT|PATCH|DELETE) (\\S+)((?: -H [^=]+=[^=]+)*)((?: -F\\[.+] [^=]+=[^=]+)*)(?<!:)$")
    public void mvcRequestWithoutBody(String method, String path, String headers, String files) {
        mvcRequest(method, path, headers, files, List.of());
    }

    @When("^(GET|POST|PUT|PATCH|DELETE) (\\S+)((?: -H [^=]+=[^=]+)*)((?: -F\\[.+] [^=]+=[^=]+)*):$")
    public void mvcRequestWithBody(String method, String path, String headers, String files,
                                   List<Map<String, String>> body) {
        mvcRequest(method, path, headers, files, body);
    }

    @SneakyThrows
    @Then("status is {int}")
    public void checkStatus(int status) {
        restHelper.getLastResult().andExpect(MockMvcResultMatchers.status().is(status));
    }

    @SneakyThrows
    private void mvcRequest(String method, String path, String headers, String files, List<Map<String, String>> body) {
        path = helpersManager.processString(path);
        List<String> headerList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headers)) {
            headerList = Arrays.stream(headers.split("-H"))
                               .map(String::trim)
                               .filter(StringUtils::isNotBlank)
                               .map(helpersManager::processString)
                               .toList();
        }
        List<String> fileList = new ArrayList<>();
        if (StringUtils.isNotEmpty(files)) {
            fileList = Arrays.stream(files.split("-F"))
                             .map(String::trim)
                             .filter(StringUtils::isNotBlank)
                             .map(helpersManager::processString)
                             .toList();
        }
        Map<String, Object> parsedBody = new HashMap<>();
        if (body != null && !body.isEmpty()) {
            for (var entry : body.get(0).entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                v = helpersManager.processString(v);
                if (v != null && (v.startsWith("{") || v.startsWith("[")))
                    parsedBody.put(k, mapper.readTree(v));
                else parsedBody.put(k, typeProcessor.parseType(v));
            }
        }
        var req = fileList.isEmpty() ? switch (method) {
            case "GET" -> MockMvcRequestBuilders.get(path);
            case "POST" -> MockMvcRequestBuilders.post(path);
            case "PUT" -> MockMvcRequestBuilders.put(path);
            case "PATCH" -> MockMvcRequestBuilders.patch(path);
            case "DELETE" -> MockMvcRequestBuilders.delete(path);
            default -> throw new IllegalStateException("Unexpected value: " + method);
        } : switch (method) {
            case "GET" -> MockMvcRequestBuilders.multipart(HttpMethod.GET, path);
            case "POST" -> MockMvcRequestBuilders.multipart(HttpMethod.POST, path);
            case "PUT" -> MockMvcRequestBuilders.multipart(HttpMethod.PUT, path);
            case "PATCH" -> MockMvcRequestBuilders.multipart(HttpMethod.PATCH, path);
            case "DELETE" -> MockMvcRequestBuilders.multipart(HttpMethod.DELETE, path);
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
        headerList.forEach(h -> {
            String[] parts = h.split("=");
            req.header(parts[0], parts[1]);
        });
        fileList.forEach(f -> {
            Matcher matcher = FILE_PATTERN.matcher(f);
            if (!matcher.matches()) return;
            String field    = matcher.group(1);
            String fileName = matcher.group(2);
            String content  = matcher.group(3);
            byte[] bytes = content.toLowerCase().startsWith("0x")
                           ? HexFormat.of().parseHex(content.substring(2)) : content.getBytes();
            MockMultipartFile file = new MockMultipartFile(
                    field, fileName, MediaType.MULTIPART_FORM_DATA_VALUE, bytes);
            ((MockMultipartHttpServletRequestBuilder) req).file(file);
        });
        if (!parsedBody.isEmpty())
            req.content(mapper.writeValueAsString(parsedBody));
        if (fileList.isEmpty())
            req.contentType(MediaType.APPLICATION_JSON);
        req.accept(MediaType.APPLICATION_JSON);
        if (contextHelper.getMockUser() != null)
            req.with(SecurityMockMvcRequestPostProcessors.user(contextHelper.getMockUser()));
        restHelper.setLastResult(mvc.perform(req).andDo(print()));
    }
}
