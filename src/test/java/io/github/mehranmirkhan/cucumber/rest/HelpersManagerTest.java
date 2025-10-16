package io.github.mehranmirkhan.cucumber.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mehranmirkhan.cucumber.rest.core.TypeProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelpersManagerTest {
    private ObjectMapper   mapper;
    private TypeProcessor  typeProcessor;
    private CucumberHelper helper;
    private HelpersManager helpersManager;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        typeProcessor = mock(TypeProcessor.class);
        helper = mock(CucumberHelper.class);
        helpersManager = new HelpersManager(List.of(helper), mapper, typeProcessor);
    }

    @Test
    void processString() {
        when(helper.processString("foo")).thenReturn("bar");
        String result = helpersManager.processString("foo");
        assertEquals("bar", result);
    }

    @Test
    void processString_emptyInput() {
        String result = helpersManager.processString("");
        assertEquals("", result);
    }

    @Test
    void parseJson_object() throws Exception {
        String json = "{\"a\":1}";
        when(helper.processString(json)).thenReturn(json);
        Object result = helpersManager.parseJson(json);
        assertEquals(1, ((com.fasterxml.jackson.databind.JsonNode) result).get("a").asInt());
    }

    @Test
    void parseJson_array() throws Exception {
        String json = "[1,2]";
        when(helper.processString(json)).thenReturn(json);
        Object result = helpersManager.parseJson(json);
        assertTrue(result instanceof com.fasterxml.jackson.databind.JsonNode);
        assertEquals(2, ((com.fasterxml.jackson.databind.JsonNode) result).size());
    }

    @Test
    void parseJson_nonJson() throws Exception {
        String input = "42";
        when(helper.processString(input)).thenReturn(input);
        when(typeProcessor.parseType(input)).thenReturn(42);
        Object result = helpersManager.parseJson(input);
        assertEquals(42, result);
    }

    @Test
    void processMap_simple() {
        Map<String, String> input = Map.of("foo", "bar");
        when(helper.processString("bar")).thenReturn("baz");
        when(helper.processString("baz")).thenReturn("baz");
        when(typeProcessor.parseType("baz")).thenReturn("baz");
        Map<String, Object> result = helpersManager.processMap(input);
        assertEquals("baz", result.get("foo"));
    }

    @Test
    void processMap_nestedKey() {
        Map<String, String> input = Map.of("foo.bar", "42");
        when(helper.processString("42")).thenReturn("42");
        when(typeProcessor.parseType("42")).thenReturn(42);
        Map<String, Object> result = helpersManager.processMap(input);
        assertTrue(result.get("foo") instanceof Map);
        assertEquals(42, ((Map<?, ?>) result.get("foo")).get("bar"));
    }

    @Test
    void processMap_multipleDotsKey() {
        Map<String, String> input = Map.of("foo.bar.baz", "99");
        when(helper.processString("99")).thenReturn("99");
        when(typeProcessor.parseType("99")).thenReturn(99);
        Map<String, Object> result = helpersManager.processMap(input);

        // Expected: {foo:{bar:{baz:99}}}
        assertInstanceOf(Map.class, result.get("foo"));
        Map<?, ?> barMap = (Map<?, ?>) result.get("foo");
        assertInstanceOf(Map.class, barMap.get("bar"));
        Map<?, ?> bazMap = (Map<?, ?>) barMap.get("bar");
        assertEquals(99, bazMap.get("baz"));
    }

    @Test
    void testProcessTable_list() {
        Map<String, String> m1 = Map.of("a", "1");
        Map<String, String> m2 = Map.of("b", "2");
        when(helper.processString("1")).thenReturn("1");
        when(helper.processString("2")).thenReturn("2");
        when(typeProcessor.parseType("1")).thenReturn(1);
        when(typeProcessor.parseType("2")).thenReturn(2);
        List<Map<String, String>> input  = List.of(m1, m2);
        List<Map<String, Object>> result = helpersManager.processTable(input);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).get("a"));
        assertEquals(2, result.get(1).get("b"));
    }

    static class Dummy {
        public int a;

        public Dummy() {}
    }

    @Test
    void parseBody() {
        Map<String, String> m = Map.of("a", "5");
        when(helper.processString("5")).thenReturn("5");
        when(typeProcessor.parseType("5")).thenReturn(5);
        HelpersManager            hm     = new HelpersManager(List.of(helper), mapper, typeProcessor);
        List<Map<String, String>> input  = List.of(m);
        List<Dummy>               result = hm.parseBody(input, Dummy.class);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).a);
    }
}