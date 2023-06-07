package com.hotelier.model.repository;

import com.hotelier.model.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepo extends JpaRepository<Feature, Long> {
}
