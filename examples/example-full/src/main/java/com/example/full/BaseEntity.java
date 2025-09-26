package com.example.full;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy
                                   ? proxy.getHibernateLazyInitializer().getPersistentClass()
                                   : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
                                      ? proxy.getHibernateLazyInitializer().getPersistentClass()
                                      : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BaseEntity that = (BaseEntity) o;
        // Persisted entities are compared by id
        // Transient entities are compared by reference
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        Class<?> effectiveClass = this instanceof HibernateProxy proxy
                                  ? proxy.getHibernateLazyInitializer().getPersistentClass()
                                  : this.getClass();
        return effectiveClass.hashCode() * 31 + (id != null ? id.hashCode() : 0);
    }
}
