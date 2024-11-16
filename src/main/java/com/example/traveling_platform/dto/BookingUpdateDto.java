package com.example.traveling_platform.dto;

import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.entities.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingUpdateDto {
    List<Long> travelPlanIds;
    List<Long> tourPlanIds;
}
