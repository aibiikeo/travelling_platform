package com.example.traveling_platform.mapper;

import com.example.traveling_platform.dto.TravelPlanDto;
import com.example.traveling_platform.entities.TravelPlanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TravelPlanMapper {
    TravelPlanEntity travelPlanDtoToTravelPlan(TravelPlanDto travelPlanDto);
    TravelPlanDto travelPlanToTravelPlanDto(TravelPlanEntity travelPlan);
}
