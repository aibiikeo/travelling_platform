package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.TourPlanUpdateDto;
import com.example.traveling_platform.entities.LandmarkEntity;
import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.LandmarkRepository;
import com.example.traveling_platform.repositories.TourPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tour-plans")
public class TourPlanController {
    @Autowired
    private TourPlanRepository tourPlanRepository;

    @Autowired
    private LandmarkRepository landmarkRepository;

    @GetMapping("/get-all")
    public List<TourPlanEntity> getAll() {
        return tourPlanRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public TourPlanEntity getById(@PathVariable("id") Long id) {
        return tourPlanRepository.findById(id).orElseThrow(() -> new ApiException("Tour plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
    }

    @PostMapping("/create")
    public TourPlanEntity create(@RequestBody TourPlanEntity newTourPlan) {
        return tourPlanRepository.save(newTourPlan);
    }


    @PutMapping("update/{id}")
    public TourPlanEntity update(@RequestBody TourPlanUpdateDto tourPlan, @PathVariable("id") Long id) {
        TourPlanEntity toUpdate = tourPlanRepository.findById(id).orElseThrow(() -> new ApiException("Tour plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
        if (tourPlan.getPlanName() != null) {
            toUpdate.setPlanName(tourPlan.getPlanName());
        }
        if (tourPlan.getStartDate() != null) {
            toUpdate.setStartDate(tourPlan.getStartDate());
        }
        if (tourPlan.getEndDate() != null) {
            toUpdate.setEndDate(tourPlan.getEndDate());
        }
        if (tourPlan.getPrice() != null){
            toUpdate.setPrice(tourPlan.getPrice());
        }
        if (tourPlan.getLandmarkIds() != null && !tourPlan.getLandmarkIds().isEmpty()) {
            List<LandmarkEntity> currentLandmarks = toUpdate.getLandmarks();
            List<LandmarkEntity> newLandmarks = landmarkRepository.findAllById(tourPlan.getLandmarkIds());
            for (LandmarkEntity newLandmark : newLandmarks) {
                if (!currentLandmarks.contains(newLandmark)) {
                    currentLandmarks.add(newLandmark);
                }
            }
            toUpdate.setLandmarks(currentLandmarks);
        }
        return tourPlanRepository.save(toUpdate);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, Object>> delete (@PathVariable Long id) {
        Optional<TourPlanEntity> tourPlan = tourPlanRepository.findById(id);
        if (tourPlan.isPresent()) {
            tourPlanRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tour plan with id " + id + " is deleted");
            response.put("deleted tour plan", tourPlan.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else throw new ApiException("Tour plan with id " + id + " is not found", HttpStatusCode.valueOf(404));
    }

    @DeleteMapping("delete-landmark/{id}")
    public TourPlanEntity deleteLandmark(@RequestBody List<Long> landmarkIds, @PathVariable("id") Long tourPlanId) {
        TourPlanEntity tourPlan = tourPlanRepository.findById(tourPlanId).orElseThrow(() -> new ApiException("Tour plan with id " + tourPlanId + " is not found", HttpStatusCode.valueOf(404)));
        List<LandmarkEntity> currentLandmarks = tourPlan.getLandmarks();
        for (Long landmarkId : landmarkIds) {
            boolean exists = currentLandmarks.stream().anyMatch(landmark -> landmark.getId().equals(landmarkId));
            if (!exists) {
                throw new ApiException("Landmark with id " + landmarkId + " is not found in this tour plan.", HttpStatusCode.valueOf(404));
            }
        }
        currentLandmarks.removeIf(landmark -> landmarkIds.contains(landmark.getId()));
        tourPlan.setLandmarks(currentLandmarks);
        return tourPlanRepository.save(tourPlan);
    }
}
