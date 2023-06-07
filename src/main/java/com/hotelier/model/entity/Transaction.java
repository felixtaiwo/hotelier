package com.hotelier.model.entity;


import com.hotelier.model.dto.TransactionDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor @Table(name = "transaction")
public class Transaction extends BaseEntity {
    @OneToMany(mappedBy = "transaction")
    private List<Invoice> invoice;
    @Basic
    @Column(name = "fee", nullable = true, precision = 0)
    private double fee;
    @Basic
    @Column(name = "amount", nullable = false, precision = 0)
    private double amount;
    @Basic
    @Column(name = "total", nullable = false, precision = 0)
    private double total;
    @Basic
    @Column(name = "status", nullable = true)
    private int status;
    @Basic
    @Column(name = "reference_id", nullable = false, length = 70)
    private String referenceId;
    @Basic
    @Column(name = "payment_id", nullable = true, length = 70)
    private String paymentId;
    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "channel", nullable = true, length = 45)
    private TransactionDto.PaymentOption channel;

    public String generateRef(Long guestId){
        return "ST-".concat(String.valueOf(guestId)).concat("-").concat(String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));
    }

}
