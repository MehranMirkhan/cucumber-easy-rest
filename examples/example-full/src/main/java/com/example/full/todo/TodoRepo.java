package com.example.full.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TodoRepo extends JpaRepository<Todo, Long>,
                                  JpaSpecificationExecutor<Todo> {
}
