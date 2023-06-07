package com.hotelier.model.repository;

import com.hotelier.model.entity.Guest;
import com.hotelier.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepo extends JpaRepository<Guest, Long> {
    Guest findByUser(User user);

}
