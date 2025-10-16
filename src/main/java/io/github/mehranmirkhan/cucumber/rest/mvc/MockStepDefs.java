package io.github.mehranmirkhan.cucumber.rest.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.core.TypeProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Mock step definitions for Cucumber tests.
 * The mocks are automatically reset after each scenario if any mocks were created.
 */
@Getter
@RequiredArgsConstructor
public class MockStepDefs {
    private final ApplicationContext ctx;
    private final ObjectMapper       mapper;
    private final TypeProcessor      typeProcessor;
    private final HelpersManager     helpersManager;

    private final Map<String, Object> originalBeans = new HashMap<>();
    private final List<Object>        mocks         = new ArrayList<>();

    @After
    @Given("^reset mocks$")
    public void resetMocks() {
        // Only reset if mocks were created
        if (originalBeans.isEmpty() && mocks.isEmpty()) {
            return;
        }

        DefaultListableBeanFactory bf =
                (DefaultListableBeanFactory) ((ConfigurableApplicationContext) ctx).getBeanFactory();

        // Restore original beans
        for (Map.Entry<String, Object> e : originalBeans.entrySet()) {
            String beanName = e.getKey();
            Object original = e.getValue();
            if (bf.containsSingleton(beanName)) {
                bf.destroySingleton(beanName);
            }
            bf.registerSingleton(beanName, original);

            // Restore original bean references in dependent beans
            updateInjectedBeans(original, ctx.getBeansOfType(Object.class).values());
        }

        // reset Mockito mocks
        if (!mocks.isEmpty()) {
            Mockito.reset(mocks.toArray());
        }

        originalBeans.clear();
        mocks.clear();
    }

    @SneakyThrows
    @Given("^mock ([\\w\\d]*).([\\w\\d]*)\\(([^)]+)\\)$")
    public void mockService(String beanName, String methodName, String arguments) {
        beanName = Introspector.decapitalize(beanName);
        Object   original = ctx.getBean(beanName);
        Class<?> svcClass = AopUtils.getTargetClass(original);

        Object mockSvc = replaceBeanWithMock(beanName, original, svcClass);

        Method   method     = findMatchingMethod(svcClass, methodName, arguments);
        Object[] argsParsed = parseArguments(method.getParameterTypes(), arguments);

        stubMockMethod(mockSvc, method, argsParsed, null);
    }

    @SneakyThrows
    @Given("^mock ([\\w\\d]*).([\\w\\d]*)\\(([^)]+)\\):$")
    public void mockService(String beanName, String methodName, String arguments,
                            List<Map<String, String>> output) {
        beanName = Introspector.decapitalize(beanName);
        Object   original = ctx.getBean(beanName);
        Class<?> svcClass = AopUtils.getTargetClass(original);

        Object mockSvc = replaceBeanWithMock(beanName, original, svcClass);

        Method   method     = findMatchingMethod(svcClass, methodName, arguments);
        Object[] argsParsed = parseArguments(method.getParameterTypes(), arguments);

        Object toReturn = buildReturnValue(method.getGenericReturnType(), output);

        stubMockMethod(mockSvc, method, argsParsed, toReturn);
    }

    protected Object replaceBeanWithMock(String beanName, Object original, Class<?> svcClass) {
        ConfigurableApplicationContext cctx    = (ConfigurableApplicationContext) ctx;
        DefaultListableBeanFactory     bf      = (DefaultListableBeanFactory) cctx.getBeanFactory();
        Object                         mockSvc = Mockito.mock(svcClass);

        if (bf.containsSingleton(beanName)) {
            bf.destroySingleton(beanName);
        }
        bf.registerSingleton(beanName, mockSvc);

        originalBeans.put(beanName, original);
        mocks.add(mockSvc);

        // Update references in dependent beans
        updateInjectedBeans(mockSvc, ctx.getBeansOfType(Object.class).values());

        return mockSvc;
    }

    protected void updateInjectedBeans(Object newBean, Collection<Object> allBeans) {
        for (Object bean : allBeans) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(newBean.getClass())) {
                    ReflectionTestUtils.setField(bean, field.getName(), newBean);
                }
            }
        }
    }

    protected Method findMatchingMethod(Class<?> svcClass, String methodName, String arguments) throws NoSuchMethodException {
        String[] parts = arguments.isEmpty() ? new String[0] : arguments.split("\\s*,\\s*");
        return Arrays.stream(svcClass.getMethods())
                     .filter(x -> x.getName().equals(methodName) && x.getParameterCount() == parts.length)
                     .findFirst()
                     .orElseThrow(() -> new NoSuchMethodException(methodName));
    }

    protected Object[] parseArguments(Class<?>[] parameterTypes, String arguments) {
        String[] parts      = arguments.isEmpty() ? new String[0] : arguments.split("\\s*,\\s*");
        Object[] argsParsed = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            String s = parts[i];
            if (Objects.equals(s, "any")) {
                argsParsed[i] = ArgumentMatchers.any();
            } else if (parameterTypes[i] == Long.class || parameterTypes[i] == long.class) {
                argsParsed[i] = Long.valueOf(s);
            } else if (parameterTypes[i] == Integer.class || parameterTypes[i] == int.class) {
                argsParsed[i] = Integer.valueOf(s);
            } else if (parameterTypes[i] == Boolean.class || parameterTypes[i] == boolean.class) {
                argsParsed[i] = Boolean.valueOf(s);
            } else {
                argsParsed[i] = s;
            }
        }

        return argsParsed;
    }

    @SneakyThrows
    protected Object buildReturnValue(Type returnType, List<Map<String, String>> output) {
        List<Map<String, Object>> parsedOutput = helpersManager.processTable(output);
        switch (returnType) {
            case ParameterizedType paraRetType when paraRetType.getRawType() == List.class -> {
                Class<?> itemCls = (Class<?>) paraRetType.getActualTypeArguments()[0];
                return mapper.convertValue(parsedOutput, mapper.getTypeFactory().constructCollectionType(List.class,
                                                                                                         itemCls));
            }
            case ParameterizedType paraRetType when paraRetType.getRawType() == Set.class -> {
                Class<?> itemCls = (Class<?>) paraRetType.getActualTypeArguments()[0];
                return mapper.convertValue(parsedOutput, mapper.getTypeFactory().constructCollectionType(Set.class,
                                                                                                         itemCls));
            }
            case ParameterizedType paraRetType when paraRetType.getRawType() == Map.class -> {
                Type keyType   = paraRetType.getActualTypeArguments()[0];
                Type valueType = paraRetType.getActualTypeArguments()[1];
                return mapper.convertValue(parsedOutput, mapper.getTypeFactory().constructMapLikeType(
                        Map.class,
                        mapper.getTypeFactory().constructType(keyType),
                        mapper.getTypeFactory().constructType(valueType)));
            }
            case null, default -> {
                return mapper.convertValue(parsedOutput.getFirst(), ((Class<?>) returnType));
            }
        }
    }

    @SneakyThrows
    protected void stubMockMethod(Object mockSvc, Method method, Object[] argsParsed, Object toReturn) {
        if (toReturn != null) {
            Mockito.when(method.invoke(mockSvc, argsParsed)).thenReturn(toReturn);
        } else {
            Mockito.when(method.invoke(mockSvc, argsParsed))
                   .thenAnswer(args -> null);
        }
    }
}
