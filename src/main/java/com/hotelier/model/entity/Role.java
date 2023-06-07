package com.hotelier.model.entity;

import com.hotelier.model.enums.SystemRoles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString @Table(name = "role")
@RequiredArgsConstructor
public class Role extends BaseEntity{
    @Basic @Enumerated(EnumType.STRING)
    @Column(name = "title", nullable = false, length = 45, unique = false)
    private SystemRoles title;

}
