package com.example.full.core.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long>,
                                  JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
}
