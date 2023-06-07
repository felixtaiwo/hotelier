package com.hotelier.model.repository;

import com.hotelier.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepo extends JpaRepository<Property, Long> {



}
