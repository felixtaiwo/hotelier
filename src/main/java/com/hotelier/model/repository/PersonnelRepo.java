package com.hotelier.model.repository;

import com.hotelier.model.entity.Personnel;
import com.hotelier.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelRepo extends JpaRepository<Personnel, Long> {
    Personnel findByUser(User user);
}
