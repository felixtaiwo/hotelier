package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor @Table(name = "room")
public class Room extends BaseEntity {
    @Basic
    @Column(name = "number", nullable = false, length = 45)
    private String number;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category", nullable = false)
    private RoomCategory category;
}
