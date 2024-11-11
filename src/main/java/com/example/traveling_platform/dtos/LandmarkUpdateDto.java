package com.example.traveling_platform.dtos;

import lombok.*;
@Data
@Builder
public class LandmarkUpdateDto {
    String title;

    String description;

    String location;

    String imageUrl;
}
