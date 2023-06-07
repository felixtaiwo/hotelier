package com.hotelier.model.repository;

import com.hotelier.model.entity.Room;
import com.hotelier.model.entity.RoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepo extends JpaRepository<Room, Long> {
    List<Room> findByCategory(RoomCategory category);
    @Query("SELECT r FROM Room r WHERE r.number LIKE %:roomNumber%")
    List<Room> findByNumber(String roomNumber);
}
