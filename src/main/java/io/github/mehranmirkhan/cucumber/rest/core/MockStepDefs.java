package io.github.mehranmirkhan.cucumber.rest.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import io.cucumber.java.en.Given;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class MockStepDefs {
    private final ApplicationContext ctx;
    private final ObjectMapper       mapper;
    private final HelpersManager     helpersManager;

    private final Map<String, Object> originalBeans = new HashMap<>();
    private final List<Object>        mocks         = new ArrayList<>();

    @SneakyThrows
    @Given("^mock ([\\w\\d]*).([\\w\\d]*)\\(([^)]+)\\):$")
    public void mockService(String serviceName, String methodName, String arguments,
                            List<Map<String, String>> output) {
        // 1) build beanName + load class
        String   beanName = Introspector.decapitalize(serviceName);
        Object   original = ctx.getBean(beanName);
        Class<?> svcClass = AopUtils.getTargetClass(original);

        // 2) replace real bean with mock
        ConfigurableApplicationContext cctx    = (ConfigurableApplicationContext) ctx;
        DefaultListableBeanFactory     bf      = (DefaultListableBeanFactory) cctx.getBeanFactory();
        Object                         mockSvc = Mockito.mock(svcClass);
        if (bf.containsSingleton(beanName)) {
            bf.destroySingleton(beanName);
        }
        bf.registerSingleton(beanName, mockSvc);

        originalBeans.put(beanName, original);
        mocks.add(mockSvc);

        // 3) parse args
        String[] parts = arguments.isEmpty()
                         ? new String[0]
                         : arguments.split("\\s*,\\s*");
        // find matching method by name+param‑count
        Method m = Arrays.stream(svcClass.getMethods())
                         .filter(x -> x.getName().equals(methodName)
                                      && x.getParameterCount() == parts.length)
                         .findFirst()
                         .orElseThrow(() -> new NoSuchMethodException(methodName));
        Class<?>[] ptypes     = m.getParameterTypes();
        Object[]   argsParsed = new Object[ptypes.length];
        for (int i = 0; i < ptypes.length; i++) {
            String s = parts[i];
            if (Objects.equals(s, "any")) argsParsed[i] = ArgumentMatchers.any();
            else if (ptypes[i] == Long.class || ptypes[i] == long.class) argsParsed[i] = Long.valueOf(s);
            else if (ptypes[i] == Integer.class || ptypes[i] == int.class) argsParsed[i] = Integer.valueOf(s);
            else if (ptypes[i] == Boolean.class || ptypes[i] == boolean.class) argsParsed[i] = Boolean.valueOf(s);
            else argsParsed[i] = s;
        }

        // 4) build return value(s)
        Type   retType = m.getGenericReturnType();
        Object toReturn;
        if (retType instanceof ParameterizedType paraRetType
            && paraRetType.getRawType() == List.class) {
            // List<T> → convert each row
            Class<?>              itemCls      = (Class<?>) paraRetType.getActualTypeArguments()[0];
            Map<String, JsonNode> parsedOutput = new HashMap<>();
            if (output != null && !output.isEmpty()) {
                for (var entry : output.get(0).entrySet()) {
                    String k = entry.getKey();
                    String v = entry.getValue();
                    v = helpersManager.processString(v);
                    if (v != null && (v.startsWith("{") || v.startsWith("[")))
                        parsedOutput.put(k, mapper.readTree(v));
                    else parsedOutput.put(k, TextNode.valueOf(v));
                }
            }
            toReturn = output.stream()
                             .map(row -> mapper.convertValue(row, itemCls))
                             .toList();
        } else {
            // single-object return
            toReturn = mapper.convertValue(output.getFirst(), m.getReturnType());
        }

        // 5) stub mock via reflection
        // calling method.invoke(mockSvc, …) is intercepted by Mockito
        Mockito.when(m.invoke(mockSvc, argsParsed)).thenReturn(toReturn);
    }

    @Given("^reset mocks$")
    public void resetMocks() {
        DefaultListableBeanFactory bf =
                (DefaultListableBeanFactory) ((ConfigurableApplicationContext) ctx).getBeanFactory();

        // restore originals
        for (Map.Entry<String, Object> e : originalBeans.entrySet()) {
            String beanName = e.getKey();
            Object original = e.getValue();
            if (bf.containsSingleton(beanName)) {
                bf.destroySingleton(beanName);
            }
            bf.registerSingleton(beanName, original);
        }

        // reset Mockito mocks
        if (!mocks.isEmpty()) {
            Mockito.reset(mocks.toArray());
        }

        originalBeans.clear();
        mocks.clear();
    }
}
