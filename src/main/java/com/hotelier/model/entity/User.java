package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity @Getter @Setter @ToString
@RequiredArgsConstructor @Table(name = "user")
public class User extends BaseEntity {
    @Basic
    @Column(name = "firstname", nullable = false, length = 45)
    private String firstname;
    @Basic
    @Column(name = "lastname", nullable = false, length = 45)
    private String lastname;
    @Basic
    @Column(name = "email_address", nullable = false, length = 45, unique = true)
    private String emailAddress;
    @Basic
    @Column(name = "password", nullable = true, length = 255)
    private String password;
    @Basic
    @Column(name = "mobile", nullable = true, length = 45)
    private String mobile;
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> roles;
}
