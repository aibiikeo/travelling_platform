package com.example.traveling_platform.repositories;

import com.example.traveling_platform.entities.TravelPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPlanRepository extends JpaRepository<TravelPlanEntity, Long> {
}
