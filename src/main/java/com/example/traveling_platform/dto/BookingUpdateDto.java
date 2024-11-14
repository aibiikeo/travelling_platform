package com.example.traveling_platform.dto;

import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.entities.UserEntity;
import lombok.*;

@Data
@Builder
public class BookingUpdateDto {
    private UserEntity user;
    private TravelPlanEntity travelPlan;
    private TourPlanEntity tourPlan;
}
