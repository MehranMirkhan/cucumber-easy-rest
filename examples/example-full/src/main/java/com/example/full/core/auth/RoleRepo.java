package com.example.full.core.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Role.Name> {
    Optional<Role> findByName(Role.Name name);
}
