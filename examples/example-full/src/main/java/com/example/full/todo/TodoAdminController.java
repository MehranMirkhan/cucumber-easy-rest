package com.example.full.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/todo")
@RequiredArgsConstructor
public class TodoAdminController {
    private final TodoService todoService;

    @GetMapping
    Page<Todo.DTO> search(Todo.DTO example,
                          TodoService.TodoFilter filter,
                          Pageable pageable) {
        return todoService.search(null, example, filter, pageable);
    }

    @GetMapping("{id}")
    Todo.DTO get(@PathVariable Long id) {
        return Todo.DTO.Mapper.INSTANCE.toDto(todoService.getById(id));
    }

    @PutMapping("{id}")
    Todo.DTO update(@PathVariable Long id,
                    @RequestBody TodoService.TodoWriteDto dto) {
        return Todo.DTO.Mapper.INSTANCE.toDto(todoService.update(id, dto));
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable Long id) {
        todoService.delete(id);
    }
}
