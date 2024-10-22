package com.example.full.core.auth;

import com.example.full.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private           String username;
    @JsonIgnore
    @Column(nullable = false)
    private transient String password;

    private Boolean disabled;
    private Boolean deleted;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DTO {
        private Long        id;
        private String      username;
        private Boolean     disabled;
        private Boolean     deleted;
        private Set<String> roles;

        @org.mapstruct.Mapper
        public interface Mapper {
            Mapper INSTANCE = Mappers.getMapper(Mapper.class);

            DTO toDto(User entity);

            @InheritInverseConfiguration
            User fromDto(DTO dto);

            @Mapping(target = ".", source = "name")
            String mapRole(Role role);

            @InheritInverseConfiguration
            Role mapRole(String role);
        }
    }
}
