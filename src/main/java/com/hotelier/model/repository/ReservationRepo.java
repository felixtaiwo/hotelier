package com.hotelier.model.repository;

import com.hotelier.model.entity.Guest;
import com.hotelier.model.entity.Reservation;
import com.hotelier.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.room = :room " +
            "AND r.checkInDate <= :endDate " +
            "AND r.checkOutDate >= :startDate " +
            "AND (r.status = 1 OR r.status = -2147483648 OR r.status = 2)"
    )
    boolean isRoomOccupiedWithinDateRange(
            @Param("room") Room room,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<Reservation> findByStatus(int i);

    Reservation findByStatusAndGuest(int i, Guest guest);

    List<Reservation> findByRoom(Room room);

    List<Reservation> findByCheckOutDate(LocalDate now);

    List<Reservation> findByGuest(Guest guest);

    @Query("SELECT COUNT(r) as count, r.status as status FROM Reservation r " +
            "WHERE r.checkInDate <= :endDate " +
            "AND r.checkOutDate >= :startDate " +
            "GROUP BY r.status"
    )
    List<Object[]> summaryDto(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
