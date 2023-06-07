package com.hotelier.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "system_prop", catalog = "")
public class SystemProp extends BaseEntity {
    @Basic
    @Column(name = "nkey", nullable = false, length = 45)
    private String nkey;
    @Basic
    @Column(name = "nvalue", nullable = false, length = 45)
    private String nvalue;
}
