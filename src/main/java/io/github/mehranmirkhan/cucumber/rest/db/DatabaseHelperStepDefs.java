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
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class DatabaseHelperStepDefs {
    private final HelpersManager      helpersManager;
    private final ContextHelper       contextHelper;
    private final EntityManager       entityManager;
    private final ListableBeanFactory beanFactory;
    private final ObjectMapper        mapper;

    @Given("^insert records for (\\w[\\w\\d]*):$")
    public void insertRecordsForTable(String entityName, List<Map<String, String>> records)
            throws IllegalAccessException {
        Class<?>      entityClass = findEntityClass(entityName);
        JpaRepository repo        = getRepository(entityClass);

        List<?> entityList = helpersManager.parseBody(records, entityClass);
        entityList = repo.saveAll(entityList);
        for (int i = 0; i < entityList.size(); i++) {
            Object entity  = entityList.get(i);
            Field  idField = FieldUtils.getFieldsWithAnnotation(entityClass, Id.class)[0];
            idField.setAccessible(true);
            Object id = idField.get(entity);
            contextHelper.getVariables().put("id" + i + 1, String.valueOf(id));
        }
    }

    @Given("^upsert records for (\\w[\\w\\d]*):$")
    public void upsertRecordsForTable(String entityName, List<Map<String, String>> records)
            throws IllegalAccessException {
        Class<?>      entityClass = findEntityClass(entityName);
        JpaRepository repo        = getRepository(entityClass);

        List<Map<String, Object>> processedRecords = helpersManager.processBody(records);
        List<?>                   entityList       = helpersManager.parseBody(records, entityClass);
        for (int i = 0; i < entityList.size(); i++) {
            Object entity  = entityList.get(i);
            Field  idField = FieldUtils.getFieldsWithAnnotation(entityClass, Id.class)[0];
            idField.setAccessible(true);
            Object   id                 = idField.get(entity);
            Optional optReferenceEntity = repo.findById(id);
            if (optReferenceEntity.isEmpty()) {
                repo.save(entity);
            } else {
                Object referenceEntity = optReferenceEntity.get();
                processedRecords.get(i).forEach((k, v) -> {
                    Field field = FieldUtils.getField(entityClass, k, true);
                    if (Objects.equals(field.getName(), idField.getName()))
                        return;
                    try {
                        field.set(referenceEntity, mapper.convertValue(v, field.getType()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
                repo.save(referenceEntity);
            }
            contextHelper.getVariables().put("id" + i + 1, String.valueOf(id));
        }
    }

    @Given("^update records for (\\w[\\w\\d]*):$")
    public void updateRecordsForTable(String entityName, List<Map<String, String>> records)
            throws IllegalAccessException {
        Class<?>      entityClass = findEntityClass(entityName);
        JpaRepository repo        = getRepository(entityClass);

        List<Map<String, Object>> processedRecords = helpersManager.processBody(records);
        List<?>                   entityList       = helpersManager.parseBody(records, entityClass);
        for (int i = 0; i < entityList.size(); i++) {
            Object entity  = entityList.get(i);
            Field  idField = FieldUtils.getFieldsWithAnnotation(entityClass, Id.class)[0];
            idField.setAccessible(true);
            Object id              = idField.get(entity);
            Object referenceEntity = repo.getReferenceById(id);
            processedRecords.get(i).forEach((k, v) -> {
                Field field = FieldUtils.getField(entityClass, k, true);
                if (Objects.equals(field.getName(), idField.getName()))
                    return;
                try {
                    field.set(referenceEntity, mapper.convertValue(v, field.getType()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            repo.save(referenceEntity);
            contextHelper.getVariables().put("id" + i + 1, String.valueOf(id));
        }
    }

    @Given("^delete records for (\\w[\\w\\d]*)$")
    public void deleteAllRecordsForTable(String entityName) {
        deleteRecordsForTable(entityName, List.of());
    }

    @Given("^delete records for (\\w[\\w\\d]*):$")
    public void deleteRecordsForTable(String entityName, List<Map<String, String>> examples) {
        Class<?>      entityClass = findEntityClass(entityName);
        JpaRepository repo        = getRepository(entityClass);

        if (examples.isEmpty()) {
            repo.deleteAll();
            return;
        }

        List<?> exampleList = helpersManager.parseBody(examples, entityClass);
        var matcher = ExampleMatcher.matching()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                    .withIgnoreNullValues()
                                    .withIgnoreCase();
        List<?> entities = exampleList.stream()
                                      .map(example -> mapper.convertValue(example, entityClass))
                                      .map(entity -> Example.of(entity, matcher))
                                      .map(example -> repo.findAll(example))
                                      .flatMap(Collection::stream)
                                      .toList();
        repo.deleteAll(entities);
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
