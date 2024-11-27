package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.TravelPlanDto;
import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.entities.UserEntity;
import com.example.traveling_platform.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @GetMapping("/all-tour-plans")
    public ResponseEntity<List<Map<String, Object>>> getAllTourPlans() {
        List<Map<String, Object>> response = bookingService.getAllTourPlans();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tour/{userId}")
    public ResponseEntity<List<TourPlanEntity>> getUserTourPlans(@PathVariable Long userId) {
        List<TourPlanEntity> tourPlans = bookingService.getUserTourPlans(userId);
        return ResponseEntity.ok(tourPlans);
    }

    @PostMapping("/tour/{userId}/{tourPlanId}")
    public ResponseEntity<String> addTourPlan(@PathVariable Long userId, @PathVariable Long tourPlanId) {
        bookingService.addTourPlan(userId, tourPlanId);
        return ResponseEntity.ok("Tour plan added");
    }

    @PutMapping("/tour/{userId}")
    public ResponseEntity<UserEntity> updateTourPlan(@PathVariable Long userId, @RequestBody List<Long> tourPlanIds) {
        UserEntity updatedUser = bookingService.updateTourPlan(userId, tourPlanIds);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/tour/{userId}/{tourPlanId}")
    public ResponseEntity<String> deleteTourPlan(@PathVariable Long userId, @PathVariable Long tourPlanId) {
        bookingService.deleteTourPlan(userId, tourPlanId);
        return ResponseEntity.ok("Tour plan deleted");
    }

    @GetMapping("/all-travel-plans")
    public ResponseEntity<List<Map<String, Object>>> getTravelPlansWithUsers() {
        List<Map<String, Object>> response = bookingService.getAllTravelPlans();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/travel/{userId}")
    public ResponseEntity<List<TravelPlanEntity>> getUserTravelPlans(@PathVariable Long userId) {
        List<TravelPlanEntity> travelPlans = bookingService.getUserTravelPlans(userId);
        return ResponseEntity.ok(travelPlans);
    }

    @PostMapping("/travel/{userId}")
    public ResponseEntity<String> addTravelPlan(@PathVariable Long userId, @RequestBody TravelPlanDto travelPlan) {
        bookingService.addTravelPlan(userId, travelPlan);
        return ResponseEntity.ok("Travel plan added");
    }

    @PutMapping("/travel/{userId}/{travelPlanId}")
    public ResponseEntity<TravelPlanEntity> updateTravelPlan(@PathVariable Long userId, @PathVariable Long travelPlanId, @RequestBody TravelPlanDto travelPlanDto) {
        TravelPlanEntity updatedTravelPlan = bookingService.updateTravelPlan(userId, travelPlanId, travelPlanDto);
        return ResponseEntity.ok(updatedTravelPlan);
    }

    @DeleteMapping("/travel/{userId}/{travelPlanId}")
    public ResponseEntity<String> deleteTravelPlan(@PathVariable Long userId, @PathVariable Long travelPlanId) {
        bookingService.deleteTravelPlan(userId, travelPlanId);
        return ResponseEntity.ok("Travel plan deleted");
    }
}
