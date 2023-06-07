package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Getter @Setter
@RequiredArgsConstructor @Table(name = "state")
public class State extends BaseEntity {
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "country", nullable = false)
    private Country country;
}
