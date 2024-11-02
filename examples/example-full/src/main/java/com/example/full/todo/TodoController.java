package com.example.full.todo;

import com.example.full.core.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("todo")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @GetMapping
    Page<Todo.DTO> search(@AuthenticationPrincipal Principal principal,
                          Todo.DTO example,
                          TodoService.TodoFilter filter,
                          Pageable pageable) {
        return todoService.search(principal.getUsername(), example, filter, pageable);
    }

    @GetMapping("{id}")
    Todo.DTO get(@AuthenticationPrincipal Principal principal,
                 @PathVariable Long id) {
        return Todo.DTO.Mapper.INSTANCE.toDto(todoService.getById(id, principal.getUsername()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Todo.DTO create(@AuthenticationPrincipal Principal principal,
                    @RequestBody TodoService.TodoWriteDto dto) {
        return Todo.DTO.Mapper.INSTANCE.toDto(todoService.create(principal.getUsername(), dto));
    }

    @PutMapping("{id}")
    Todo.DTO update(@AuthenticationPrincipal Principal principal,
                    @PathVariable Long id,
                    @RequestBody TodoService.TodoWriteDto dto) {
        return Todo.DTO.Mapper.INSTANCE.toDto(todoService.update(id, dto, principal.getUsername()));
    }

    @DeleteMapping("{id}")
    void delete(@AuthenticationPrincipal Principal principal,
                @PathVariable Long id) {
        todoService.delete(id, principal.getUsername());
    }
}
