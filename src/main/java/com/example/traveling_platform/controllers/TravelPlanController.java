package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.TravelPlanUpdateDto;
import com.example.traveling_platform.entities.LandmarkEntity;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.LandmarkRepository;
import com.example.traveling_platform.repositories.TravelPlanRepository;
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
@RequestMapping("/travel-plans")
public class TravelPlanController {

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private LandmarkRepository landmarkRepository;

    @GetMapping("/get-all")
    public List<TravelPlanEntity> getAll() {
        return travelPlanRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public TravelPlanEntity getById(@PathVariable("id") Long id) {
        return travelPlanRepository.findById(id).orElseThrow(() -> new ApiException("Travel plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
    }

    @PostMapping("/create")
    public TravelPlanEntity create(@RequestBody TravelPlanEntity newTravelPlan) {
        return travelPlanRepository.save(newTravelPlan);
    }

    @PutMapping("update/{id}")
    public TravelPlanEntity update(@RequestBody TravelPlanUpdateDto travelPlan, @PathVariable("id") Long id) {
        TravelPlanEntity toUpdate = travelPlanRepository.findById(id).orElseThrow(() -> new ApiException("Travel plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
        if (travelPlan.getPlanName() != null) {
            toUpdate.setPlanName(travelPlan.getPlanName());
        }
        if (travelPlan.getStartDate() != null) {
            toUpdate.setStartDate(travelPlan.getStartDate());
        }
        if (travelPlan.getEndDate() != null) {
            toUpdate.setEndDate(travelPlan.getEndDate());
        }
        if (travelPlan.getPrice() != null){
            toUpdate.setPrice(travelPlan.getPrice());
        }
        if (travelPlan.getLandmarkIds() != null && !travelPlan.getLandmarkIds().isEmpty()) {
            List<LandmarkEntity> currentLandmarks = toUpdate.getLandmarks();
            List<LandmarkEntity> newLandmarks = landmarkRepository.findAllById(travelPlan.getLandmarkIds());
            for (LandmarkEntity newLandmark : newLandmarks) {
                if (!currentLandmarks.contains(newLandmark)) {
                    currentLandmarks.add(newLandmark);
                }
            }
            toUpdate.setLandmarks(currentLandmarks);
        }
        return travelPlanRepository.save(toUpdate);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, Object>> delete (@PathVariable Long id) {
        Optional<TravelPlanEntity> travelPlan = travelPlanRepository.findById(id);
        if (travelPlan.isPresent()) {
            travelPlanRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Travel plan with id " + id + " is deleted");
            response.put("deleted travel plan", travelPlan.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else throw new ApiException("Travel plan with id " + id + " is not found", HttpStatusCode.valueOf(404));
    }

    @DeleteMapping("delete-landmark/{id}")
    public TravelPlanEntity deleteLandmark(@RequestBody List<Long> landmarkIds, @PathVariable("id") Long travelPlanId) {
        TravelPlanEntity travelPlan = travelPlanRepository.findById(travelPlanId).orElseThrow(() -> new ApiException("Travel plan with id " + travelPlanId + " is not found", HttpStatusCode.valueOf(404)));
        List<LandmarkEntity> currentLandmarks = travelPlan.getLandmarks();
        for (Long landmarkId : landmarkIds) {
            boolean exists = currentLandmarks.stream().anyMatch(landmark -> landmark.getId().equals(landmarkId));
            if (!exists) {
                throw new ApiException("Landmark with id " + landmarkId + " is not found in this travel plan.", HttpStatusCode.valueOf(404));
            }
        }
        currentLandmarks.removeIf(landmark -> landmarkIds.contains(landmark.getId()));
        travelPlan.setLandmarks(currentLandmarks);
        return travelPlanRepository.save(travelPlan);
    }

}
