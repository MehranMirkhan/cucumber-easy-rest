package com.example.full.core.auth;

import com.example.full.Params;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
public class UsersInitializer {
    private final Params   params;
    private final UserRepo userRepo;
    private final UserService userService;

    @PostConstruct
    public void init() {
        log.info(">>> Initializing users");
        if (userRepo.count() != 0) return;
        UserService.UserCreateDto createDto = UserService.UserCreateDto
                .builder()
                .username(params.auth().defaultAdminUsername())
                .password(params.auth().defaultAdminPassword())
                .roles(Set.of(Role.Name.ROLE_ADMIN.name()))
                .build();
        userService.create(createDto);
    }
}
