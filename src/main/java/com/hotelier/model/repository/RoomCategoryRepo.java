package com.hotelier.model.repository;

import com.hotelier.model.entity.Property;
import com.hotelier.model.entity.RoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomCategoryRepo extends JpaRepository<RoomCategory, Long> {
    List<RoomCategory> findByProperty(Property p);
    @Query("SELECT r FROM RoomCategory r WHERE r.price>=:minPrice AND r.price<=:maxPrice AND r.name LIKE %:name%")
    List<RoomCategory> filterCategory(double minPrice, double maxPrice, String name);
    @Query("SELECT r FROM RoomCategory r WHERE r.price>=:minPrice AND r.price<=:maxPrice AND r.property=:property AND r.name LIKE %:name%")
    List<RoomCategory> filterCategory(double minPrice, double maxPrice, Property property, String name);
}
