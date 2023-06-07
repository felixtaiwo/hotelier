package com.hotelier.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Persistable<Long>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime createDate = getCurrentDateTime();

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified = getCurrentDateTime();

    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    @PreUpdate
    public void logLastModified() {
        this.lastModified = getCurrentDateTime();
    }

    public boolean isNew() {
        return false;
    }

}
