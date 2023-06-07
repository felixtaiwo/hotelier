package com.hotelier.model.repository;

import com.hotelier.model.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepo extends JpaRepository<State, Long> {
}
