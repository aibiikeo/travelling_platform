package com.example.traveling_platform.mapper;

import com.example.traveling_platform.dto.TourPlanDto;
import com.example.traveling_platform.entities.TourPlanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TourPlanMapper {
    TourPlanEntity tourPlanDtoToTourPlan(TourPlanDto tourPlanDto);
    TourPlanDto tourPlanToTourPlanDto(TourPlanEntity tourPlan);
}
