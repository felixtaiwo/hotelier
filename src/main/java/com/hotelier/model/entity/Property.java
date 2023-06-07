package com.hotelier.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "property")
public class Property  extends BaseEntity{
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic
    @Column(name = "postal_code", nullable = true, length = 45)
    private String postalCode;
    @Basic
    @Column(name = "city", nullable = true, length = 45)
    private String city;
    @Basic
    @Column(name = "full_address", nullable = true, length = 45)
    private String fullAddress;
    @ManyToOne
    @JoinColumn(name = "state_addr", nullable = false)
    private State stateAddr;
    @Basic
    @Column(name = "support_email", nullable = true, length = 45)
    private String supportEmail;
    @Basic
    @Column(name = "support_phone", nullable = true, length = 45)
    private String supportPhone;

    @Basic @Max(5) @Min(0)
    @Column(name = "rating", nullable = true, length = 45)
    private Float rating;
    @Basic
    @Column(name = "check_out_time", nullable = false)
    private LocalTime checkInTime = LocalTime.of(0,0);
    @Basic
    @Column(name = "check_in_time", nullable = false)
    private LocalTime checkOutTime = LocalTime.of(23,59,59);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company", nullable = false)
    private Company company;
    @ManyToMany(mappedBy = "propertyList")
    private List<Personnel> personnelList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Property property = (Property) o;
        return getId() != null && Objects.equals(getId(), property.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
