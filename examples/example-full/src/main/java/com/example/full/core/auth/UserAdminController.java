package com.example.full.core.auth;

import com.example.full.core.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/user")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    PageDto<User.DTO> search(User.DTO example,
                             UserService.UserFilter filter,
                             Pageable pageable) {
        return PageDto.of(userService.search(example, filter, pageable));
    }

    @GetMapping("{id}")
    User.DTO get(@PathVariable Long id) {
        return User.DTO.Mapper.INSTANCE.toDto(userService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    User.DTO create(@RequestBody UserService.UserCreateDto dto) {
        return User.DTO.Mapper.INSTANCE.toDto(userService.create(dto));
    }

    @PutMapping("{id}")
    User.DTO update(@PathVariable Long id,
                    @RequestBody UserService.UserUpdateDto dto) {
        return User.DTO.Mapper.INSTANCE.toDto(userService.update(id, dto));
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
