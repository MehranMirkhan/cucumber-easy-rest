package com.example.full.todo;

import com.example.full.core.auth.UserService;
import com.example.full.core.auth.User_;
import com.example.full.core.error.NotFoundException;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepo    todoRepo;
    private final UserService userService;

    /**
     * Search in Todos.
     *
     * @param restrictionUsername Restrict the result to records related to this user
     * @param example             An example entity to search by
     * @param filter              Additional filters
     * @param pageable            Pagination information
     * @return A paginated list of related entities
     */
    public Page<Todo.DTO> search(String restrictionUsername,
                                 Todo.DTO example,
                                 TodoFilter filter,
                                 Pageable pageable) {
        var matcher = ExampleMatcher.matching()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                    .withIgnoreNullValues()
                                    .withIgnoreCase();
        var entity        = Todo.DTO.Mapper.INSTANCE.fromDto(example);
        var exampleEntity = Example.of(entity, matcher);
        Specification<Todo> spec = (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            // Add example-based predicate
            Predicate examplePredicate = QueryByExamplePredicateBuilder.getPredicate(root, builder, exampleEntity);
            if (examplePredicate != null) predicates.add(examplePredicate);
            // Restriction
            if (StringUtils.isNotEmpty(restrictionUsername)) {
                predicates.add(builder.equal(root.get(Todo_.owner).get(User_.username),
                                             restrictionUsername));
            }
            return builder.and(predicates.toArray(new Predicate[]{}));
        };
        return todoRepo.findAll(spec, pageable).map(Todo.DTO.Mapper.INSTANCE::toDto);
    }

    public Todo getById(@NotNull Long id) {
        return getById(id, null);
    }

    public Todo getById(@NotNull Long id, String restrictionUsername) {
        return todoRepo.findById(id)
                       .filter(todo -> StringUtils.isEmpty(restrictionUsername)
                                       || todo.getOwner().getUsername().equals(restrictionUsername))
                       .orElseThrow(() -> new NotFoundException("Todo not found"));
    }

    public Todo create(@NotEmpty String username, @NotNull TodoWriteDto dto) {
        Todo todo = Todo.builder().text(dto.text).done(dto.done).priority(dto.priority)
                        .owner(userService.getByUsername(username)).build();
        return todoRepo.save(todo);
    }

    public Todo update(@NotNull Long id, @NotNull TodoWriteDto dto) {
        return update(id, dto, null);
    }

    public Todo update(@NotNull Long id, @NotNull TodoWriteDto dto, String restrictionUsername) {
        Todo todo = getById(id);
        if (StringUtils.isNotEmpty(restrictionUsername)
            && !todo.getOwner().getUsername().equals(restrictionUsername)) {
            throw new NotFoundException("Todo not found");
        }
        todo.setText(dto.text);
        todo.setDone(dto.done);
        return todoRepo.save(todo);
    }

    public void delete(@NotNull Long id) {
        delete(id, null);
    }

    public void delete(@NotNull Long id, String restrictionUsername) {
        Todo todo = getById(id);
        if (StringUtils.isNotEmpty(restrictionUsername)
            && !todo.getOwner().getUsername().equals(restrictionUsername)) {
            throw new NotFoundException("Todo not found");
        }
        todoRepo.delete(todo);
    }

    public record TodoFilter() {}

    public record TodoWriteDto(String text, Boolean done, Integer priority) {}
}
