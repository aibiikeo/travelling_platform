package com.example.traveling_platform.services;

import com.example.traveling_platform.dto.TravelPlanDto;
import com.example.traveling_platform.entities.LandmarkEntity;
import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.entities.UserEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.LandmarkRepository;
import com.example.traveling_platform.repositories.TourPlanRepository;
import com.example.traveling_platform.repositories.TravelPlanRepository;
import com.example.traveling_platform.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourPlanRepository tourPlanRepository;

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private TravelPlanService travelPlanService;

    @Autowired
    private LandmarkRepository landmarkRepository;

//    public void addTravelPlan(Long userId, TravelPlanEntity travelPlan) {
//        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
//        travelPlanRepository.save(travelPlan);
//        user.getTravelPlan().add(travelPlan);
//        userRepository.save(user);
//    }

    public void addTravelPlan(Long userId, TravelPlanDto travelPlanDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
        TravelPlanEntity travelPlan = new TravelPlanEntity();
        travelPlan.setPlanName(travelPlanDto.getPlanName());
        travelPlan.setStartDate(travelPlanDto.getStartDate());
        travelPlan.setEndDate(travelPlanDto.getEndDate());
        List<LandmarkEntity> landmarks = landmarkRepository.findAllById(travelPlanDto.getLandmarkIds());
        travelPlan.setLandmarks(landmarks);
        travelPlan.updatePrice();
        travelPlanRepository.save(travelPlan);
        user.getTravelPlan().add(travelPlan);
        userRepository.save(user);
    }


    public void addTourPlan(Long userId, Long tourPlanId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
        TourPlanEntity tourPlan = tourPlanRepository.findById(tourPlanId).orElseThrow(() -> new ApiException("Tour plan with id " + tourPlanId + " is not found", HttpStatusCode.valueOf(404)));
        user.getTourPlan().add(tourPlan);
        userRepository.save(user);
    }

    public List<Map<String, Object>> getAllTourPlans() {
        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .flatMap(user -> user.getTourPlan().stream()
                        .map(plan -> {
                            Map<String, Object> result = new HashMap<>();
                            Map<String, Object> tourPlan = new HashMap<>();
                            tourPlan.put("id", plan.getId());
                            tourPlan.put("planName", plan.getPlanName());
                            tourPlan.put("startDate", plan.getStartDate());
                            tourPlan.put("endDate", plan.getEndDate());
                            tourPlan.put("price", plan.getPrice());
                            tourPlan.put("landmarks", plan.getLandmarks().stream().map(landmark -> {
                                Map<String, Object> landmarkDto = new HashMap<>();
                                landmarkDto.put("id", landmark.getId());
                                landmarkDto.put("title", landmark.getTitle());
                                landmarkDto.put("description", landmark.getDescription());
                                landmarkDto.put("location", landmark.getLocation());
                                landmarkDto.put("price", landmark.getPrice());
                                landmarkDto.put("imageUrl", landmark.getImageUrl());
                                return landmarkDto;
                            }).collect(Collectors.toList()));
                            result.put("tourPlan", tourPlan);
                            result.put("user", user.getId());
                            return result;
                        })
                )
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllTravelPlans() {
        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .flatMap(user -> user.getTravelPlan().stream()
                        .map(plan -> {
                            Map<String, Object> result = new HashMap<>();
                            Map<String, Object> travelPlan = new HashMap<>();
                            travelPlan.put("id", plan.getId());
                            travelPlan.put("planName", plan.getPlanName());
                            travelPlan.put("startDate", plan.getStartDate());
                            travelPlan.put("endDate", plan.getEndDate());
                            travelPlan.put("price", plan.getPrice());
                            travelPlan.put("landmarks", plan.getLandmarks().stream().map(landmark -> {
                                Map<String, Object> landmarkDto = new HashMap<>();
                                landmarkDto.put("id", landmark.getId());
                                landmarkDto.put("title", landmark.getTitle());
                                landmarkDto.put("description", landmark.getDescription());
                                landmarkDto.put("location", landmark.getLocation());
                                landmarkDto.put("price", landmark.getPrice());
                                landmarkDto.put("imageUrl", landmark.getImageUrl());
                                return landmarkDto;
                            }).collect(Collectors.toList()));
                            result.put("travelPlan", travelPlan);
                            result.put("user", user.getId());
                            return result;
                        })
                )
                .collect(Collectors.toList());
    }


    public List<TravelPlanEntity> getUserTravelPlans(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
        return user.getTravelPlan();
    }

    public List<TourPlanEntity> getUserTourPlans(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
        return user.getTourPlan();
    }

    public TravelPlanEntity updateTravelPlan(Long userId, Long travelPlanId, TravelPlanDto travelPlanDto) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
        boolean ownsTravelPlan = user.getTravelPlan().stream().anyMatch(travelPlan -> travelPlan.getId().equals(travelPlanId));
        if (!ownsTravelPlan) throw new ApiException("Travel plan with id " + travelPlanId + " is not owned with user id " + userId, HttpStatusCode.valueOf(403));
        return travelPlanService.update(travelPlanDto, travelPlanId);
    }

    public UserEntity updateTourPlan(Long userId, List<Long> tourPlanIds) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
        List<TourPlanEntity> newTourPlans = tourPlanRepository.findAllById(tourPlanIds);
        List<Long> foundIds = newTourPlans.stream().map(TourPlanEntity::getId).toList();
        List<Long> missingIds = tourPlanIds.stream().filter(id -> !foundIds.contains(id)).toList();
        if (!missingIds.isEmpty()) {
            throw new ApiException("Tour plans with id " + missingIds + " are not found", HttpStatusCode.valueOf(404));
        }
        List<TourPlanEntity> existingTourPlans = user.getTourPlan();
        newTourPlans.forEach(newPlan -> {
            if (!existingTourPlans.contains(newPlan)) {
                existingTourPlans.add(newPlan);
            }
        });
        user.setTourPlan(existingTourPlans);
        return userRepository.save(user);
    }


    public void deleteTravelPlan(Long userId, Long travelPlanId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
        user.getTravelPlan().removeIf(plan -> plan.getId().equals(travelPlanId));
        userRepository.save(user);
    }

    public void deleteTourPlan(Long userId, Long tourPlanId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User with id " + userId + " is not found", HttpStatusCode.valueOf(404)));
        user.getTourPlan().removeIf(plan -> plan.getId().equals(tourPlanId));
        userRepository.save(user);
    }
}