package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
@RequiredArgsConstructor
@Table(name = "guest")
public class Guest extends BaseEntity {
    @Basic
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    @ManyToOne
    @JoinColumn(name = "nationality", nullable = false)
    private Country nationality;
    @Basic
    @Column(name = "postal_code", nullable = true, length = 45)
    private String postalCode;
    @Basic
    @Column(name = "residential_city", nullable = false, length = 25)
    private String residentialCity;
    @ManyToOne
    @JoinColumn(name = "residential_state", nullable = false)
    private State residentialState;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "entityId")
    @Column(name = "file", nullable = true, length = 225)
    private List<FileStore> identity = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;
}
