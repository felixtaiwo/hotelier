package com.hotelier.model.repository;

import com.hotelier.model.entity.Invoice;
import com.hotelier.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    List<Invoice> findByReservation(Reservation reservation);
}
