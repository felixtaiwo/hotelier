package com.hotelier.model.repository;

import com.hotelier.model.entity.Role;
import com.hotelier.model.enums.SystemRoles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByTitle(SystemRoles systemRoles);
}
