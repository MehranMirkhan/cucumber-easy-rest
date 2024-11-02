package io.github.mehranmirkhan.cucumber.rest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.core.ContextHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.metamodel.EntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DatabaseHelperStepDefs {
    private final HelpersManager      helpersManager;
    private final ContextHelper       contextHelper;
    private final EntityManager       entityManager;
    private final ListableBeanFactory beanFactory;
    private final ObjectMapper        mapper;

    @Given("^records for (\\w[\\w\\d]*):$")
    public void recordsForTable(String entityName, List<Map<String, String>> records)
            throws NoSuchFieldException, IllegalAccessException {
        Class<?>      entityClass = findEntityClass(entityName);
        JpaRepository repo        = getRepository(entityClass);

        List<?> entityList = helpersManager.parseBody(records, entityClass);
        entityList = repo.saveAll(entityList);
        for (int i = 0; i < entityList.size(); i++) {
            Object entity  = entityList.get(i);
            Field  idField = FieldUtils.getFieldsWithAnnotation(entity.getClass(), Id.class)[0];
            idField.setAccessible(true);
            Object id = idField.get(entity);
            contextHelper.getVariables().put("id" + i + 1, String.valueOf(id));
        }
    }

    @Given("^find (\\w[\\w\\d]*):$")
    public void findEntityByField(String entityName, List<Map<String, String>> examples) {
        Class<?>      entityClass = findEntityClass(entityName);
        JpaRepository repo        = getRepository(entityClass);
        if (examples.size() > 1)
            throw new RuntimeException("Only one example is allowed for find operation");
        List<?> exampleList = helpersManager.parseBody(examples, entityClass);
        Object  example     = exampleList.get(0);

        var matcher = ExampleMatcher.matching()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                    .withIgnoreNullValues()
                                    .withIgnoreCase();
        var entity        = mapper.convertValue(example, entityClass);
        var exampleEntity = Example.of(entity, matcher);
        repo.findOne(exampleEntity)
            .ifPresentOrElse(entity1 -> {
                try {
                    Field idField = FieldUtils.getFieldsWithAnnotation(entity1.getClass(), Id.class)[0];
                    idField.setAccessible(true);
                    Object id = idField.get(entity1);
                    contextHelper.getVariables().put("id", String.valueOf(id));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                throw new RuntimeException("Entity not found: " + entityName + " - " + example);
            });
    }

    protected Class<?> findEntityClass(String entityName) {
        List<EntityType<?>> types = entityManager.getMetamodel().getEntities()
                                                 .stream()
                                                 .filter(type -> entityName.equalsIgnoreCase(type.getName()))
                                                 .toList();
        if (types.isEmpty()) throw new RuntimeException("Entity not found: " + entityName);
        if (types.size() > 1) throw new RuntimeException("Ambiguous entity: " + entityName
                                                         + " - Found matches: " + types);
        return types.getFirst().getJavaType();
    }

    protected JpaRepository<?, ?> getRepository(Class<?> entityClass) {
        Repositories repositories = new Repositories(beanFactory);
        return (JpaRepository<?, ?>) repositories
                .getRepositoryFor(entityClass)
                .orElseThrow(() -> new RuntimeException("Repository not found for " + entityClass.getName()));
    }
}
