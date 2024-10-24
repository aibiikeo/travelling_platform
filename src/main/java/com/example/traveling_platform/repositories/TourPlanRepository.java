package com.example.traveling_platform.repositories;

import com.example.traveling_platform.entities.TourPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourPlanRepository extends JpaRepository<TourPlanEntity, Long> {
}
