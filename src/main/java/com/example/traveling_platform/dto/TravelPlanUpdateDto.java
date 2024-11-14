package com.example.traveling_platform.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class TravelPlanUpdateDto {
    String planName;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Double price;
}

