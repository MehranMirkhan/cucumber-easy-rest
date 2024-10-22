package com.example.full.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("todo")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @GetMapping
    Page<Todo.DTO> search(@AuthenticationPrincipal String username,
                          Todo.DTO example,
                          TodoService.TodoFilter filter,
                          Pageable pageable) {
        return todoService.search(username, example, filter, pageable);
    }

    @GetMapping("{id}")
    Todo.DTO get(@AuthenticationPrincipal String username,
                 @PathVariable Long id) {
        return Todo.DTO.Mapper.INSTANCE.toDto(todoService.getById(id, username));
    }

    @PostMapping
    Todo.DTO create(@AuthenticationPrincipal String username,
                    @RequestBody TodoService.TodoWriteDto dto) {
        return Todo.DTO.Mapper.INSTANCE.toDto(todoService.create(username, dto));
    }

    @PutMapping("{id}")
    Todo.DTO update(@AuthenticationPrincipal String username,
                    @PathVariable Long id,
                    @RequestBody TodoService.TodoWriteDto dto) {
        return Todo.DTO.Mapper.INSTANCE.toDto(todoService.update(id, dto, username));
    }

    @DeleteMapping("{id}")
    void delete(@AuthenticationPrincipal String username,
                @PathVariable Long id) {
        todoService.delete(id, username);
    }
}
