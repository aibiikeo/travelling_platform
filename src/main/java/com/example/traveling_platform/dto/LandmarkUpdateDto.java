package com.example.traveling_platform.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LandmarkUpdateDto {
    String title;
    String description;
    String location;
    String imageUrl;
}
