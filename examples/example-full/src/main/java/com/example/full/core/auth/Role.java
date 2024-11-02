package com.example.full.core.auth;

import com.example.full.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role extends BaseEntity {
    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private Name name;

    public enum Name {ROLE_ADMIN, ROLE_USER}
}
