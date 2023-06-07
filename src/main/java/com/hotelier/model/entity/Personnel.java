package com.hotelier.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity @Getter @Setter
@RequiredArgsConstructor
@Table(name = "personnel")
public class Personnel extends BaseEntity{
    @Basic
    @Column(name = "designation", nullable = true, length = 45)
    private String designation;
    @ManyToMany @JsonIgnore
    @JoinColumn(name = "property", nullable = true)
    private List<Property> propertyList;
    @OneToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;
}
