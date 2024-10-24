package com.example.traveling_platform.repositories;

import com.example.traveling_platform.entities.LandmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandmarkRepository extends JpaRepository<LandmarkEntity, Long> {
}
