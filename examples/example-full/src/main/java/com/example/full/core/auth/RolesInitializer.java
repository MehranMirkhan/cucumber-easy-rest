package com.example.full.core.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class RolesInitializer {
    private final RoleRepo roleRepo;

    @PostConstruct
    public void init() {
        log.info(">>> Initializing roles");
        if (roleRepo.count() != 0) return;
        List<Role> roles = Arrays.stream(Role.Name.values())
                                 .map(Role::new)
                                 .toList();
        roleRepo.saveAll(roles);
    }
}
