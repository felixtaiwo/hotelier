package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "feature")
public class Feature extends BaseEntity {
    @Basic
    @Column(name = "name", nullable = true, length = 45)
    private String name;

}
