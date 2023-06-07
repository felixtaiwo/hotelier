package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Table(name = "reservation")
@Entity @Getter @Setter @RequiredArgsConstructor
public class Reservation extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "guest", nullable = false)
    private Guest guest;
    @Basic
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;
    @Basic
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;
    @ManyToOne
    @JoinColumn(name = "room", nullable = false)
    private Room room;
    @Basic
    @Column(name = "status", nullable = false)
    private Integer status = -1;
    @Basic
    @Column(name = "purpose_of_stay", nullable = true, length = 225)
    private String purposeOfStay;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservation")
    List<Invoice> invoiceList;
}
