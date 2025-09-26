package com.example.full.todo;

import com.example.full.BaseEntity;
import com.example.full.core.auth.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.factory.Mappers;

@Table
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Todo extends BaseEntity {
    private String  text;
    private Boolean done;
    private Integer priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @PreUpdate
    @PrePersist
    private void prePersist() {
        if (done == null) done = false;
        if (priority == null) priority = 0;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DTO {
        private Long    id;
        private String  text;
        private Boolean done;
        private Integer priority;
        private String  owner;

        @org.mapstruct.Mapper
        public interface Mapper {
            Mapper INSTANCE = Mappers.getMapper(Mapper.class);

            DTO toDto(Todo entity);

            @InheritInverseConfiguration
            Todo fromDto(DTO dto);

            default String mapUser(User user) {
                if (user == null) return null;
                return user.getUsername();
            }

            default User mapUser(String username) {
                return User.builder().username(username).build();
            }
        }
    }
}
