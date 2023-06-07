package com.hotelier.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "invoice", catalog = "")
public class Invoice extends BaseEntity{
    @Basic
    @Column(name = "description", nullable = true, length = 225)
    private String description;
    @Basic
    @Column(name = "unit_price", nullable = true, precision = 0)
    private double unitPrice;
    @Basic
    @Column(name = "quantity", nullable = true, precision = 0)
    private double quantity;
    @Basic
    @Column(name = "price", nullable = true, precision = 0)
    private double price;
    @ManyToOne(fetch = FetchType.LAZY) @JsonIgnore
    private Reservation reservation;
    @ManyToOne(optional = true) @JsonIgnore
    private Transaction transaction;
}
