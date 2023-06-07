package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity @Getter @Setter
@RequiredArgsConstructor
@Table(name = "room_category", catalog = "")
public class RoomCategory extends BaseEntity {
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic
    @Column(name = "guest_size", nullable = false, length = 45)
    private int guestSize;
    @ManyToMany(fetch = FetchType.LAZY)
    List<Feature> features;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    Property property;
    @Column(name = "price", nullable = false)
    private Double price;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "entityId")
    @Column(name = "file", nullable = true, length = 225)
    private List<FileStore> files;

    public RoomCategory(Long categoryId) {
        super();
        this.setId(categoryId);
    }
}
