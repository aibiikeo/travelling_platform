package com.example.traveling_platform.repositories;

import com.example.traveling_platform.entities.LandmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LandmarkRepository extends JpaRepository<LandmarkEntity, Long> {
    Optional<LandmarkEntity> findByTitle(String title);
}
