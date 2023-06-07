package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Getter @Setter @RequiredArgsConstructor
@Table(name = "file_store")
public class FileStore extends BaseEntity {
    public enum FileStorage{
        AMAZON_S3, LOCAL_SERVER
    }
    @Lob
    @Column(name = "url")
    private String url;
    @Column(name = "metadata", nullable = true)
    @Lob
    private String metaData;
    @Basic
    @Column(name = "entity_id", nullable = false, length = 45)
    private Long entityId;
    @Basic
    @Column(name = "entity_type", nullable = false, length = 45)
    private String entityType;
    @Column(name = "file_storage", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private FileStorage fileStorage;


}
