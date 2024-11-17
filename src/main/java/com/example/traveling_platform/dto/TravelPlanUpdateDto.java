package com.example.traveling_platform.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TravelPlanUpdateDto {
    String planName;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Double price;
    List<Long> landmarkIds;
}

