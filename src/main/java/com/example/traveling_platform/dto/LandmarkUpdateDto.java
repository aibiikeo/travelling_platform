package com.example.traveling_platform.dto;

import lombok.*;
@Data
@Builder
public class LandmarkUpdateDto {
    String title;
    String description;
    String location;
    String imageUrl;
}
