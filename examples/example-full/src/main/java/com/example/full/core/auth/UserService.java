package com.example.full.core.auth;

import com.example.full.core.error.NotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepo        userRepo;
    private final RoleRepo        roleRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Search in Users.
     *
     * @param example  An example entity to search by
     * @param filter   Additional filters
     * @param pageable Pagination information
     * @return A paginated list of related entities
     */
    public Page<User.DTO> search(User.DTO example,
                                 UserFilter filter,
                                 Pageable pageable) {
        if (example == null) example = User.DTO.builder().build();
        if (filter == null) filter = UserFilter.builder().build();
        var matcher = ExampleMatcher.matching()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                    .withIgnoreNullValues()
                                    .withIgnoreCase();
        var entity        = User.DTO.Mapper.INSTANCE.fromDto(example);
        var exampleEntity = Example.of(entity, matcher);
        Specification<User> spec = (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            // Add example-based predicate
            Predicate examplePredicate = QueryByExamplePredicateBuilder.getPredicate(root, builder, exampleEntity);
            if (examplePredicate != null) predicates.add(examplePredicate);
            return builder.and(predicates.toArray(new Predicate[]{}));
        };
        return userRepo.findAll(spec, pageable).map(User.DTO.Mapper.INSTANCE::toDto);
    }

    public User getById(Long id) {
        return userRepo.findById(id)
                       .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User getByUsername(String username) {
        return userRepo.findByUsername(username)
                       .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User create(UserCreateDto dto) {
        Set<Role> roles = dto.roles.stream()
                                   .map(Role.Name::valueOf)
                                   .map(roleRepo::findByName)
                                   .map(Optional::orElseThrow)
                                   .collect(Collectors.toSet());
        User user = User.builder()
                        .username(dto.username)
                        .password(passwordEncoder.encode(dto.password))
                        .roles(roles)
                        .disabled(false)
                        .deleted(false)
                        .build();
        return userRepo.save(user);
    }

    public User update(Long id, UserUpdateDto dto) {
        Set<Role> roles = dto.roles.stream()
                                   .map(Role.Name::valueOf)
                                   .map(roleRepo::findByName)
                                   .map(Optional::orElseThrow)
                                   .collect(Collectors.toSet());
        User user = getById(id);
        user.setPassword(passwordEncoder.encode(dto.password));
        user.setRoles(roles);
        user.setDisabled(dto.disabled);
        return userRepo.save(user);
    }

    public void delete(Long id) {
        User user = getById(id);
        user.setDeleted(true);
        userRepo.save(user);
    }

    @Builder
    public record UserFilter() {}

    @Builder
    public record UserCreateDto(String username, String password, Set<String> roles) {}

    @Builder
    public record UserUpdateDto(String password, Set<String> roles, Boolean disabled) {}
}
