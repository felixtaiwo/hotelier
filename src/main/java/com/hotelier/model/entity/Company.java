package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Getter @Setter @RequiredArgsConstructor
@Table(name = "company")
public class Company extends BaseEntity {
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic @Lob
    @Column(name = "logo_url")
    private String logoUrl;
    @Basic
    @Column(name = "hq_postal_code", nullable = true, length = 45)
    private String hqPostalCode;
    @Basic
    @Column(name = "hq_city", nullable = true, length = 45)
    private String hqCity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hq_state", nullable = false)
    private State hqState;
    @Basic
    @Column(name = "support_email", nullable = true, length = 45)
    private String supportEmail;
    @Basic
    @Column(name = "support_phone", nullable = true, length = 45)
    private String supportPhone;


}
