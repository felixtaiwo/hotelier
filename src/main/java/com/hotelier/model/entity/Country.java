package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity @Getter @Setter @Table(name = "country")
@RequiredArgsConstructor
public class Country extends BaseEntity {
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic
    @Column(name = "phone_code", nullable = false, length = 45)
    private String phoneCode;
    @Basic
    @Column(name = "currency", nullable = false, length = 45)
    private String currency;
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    List<State> states;
}
