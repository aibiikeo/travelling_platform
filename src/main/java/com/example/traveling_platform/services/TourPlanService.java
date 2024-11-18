package com.example.traveling_platform.services;

import com.example.traveling_platform.dto.TourPlanUpdateDto;
import com.example.traveling_platform.entities.LandmarkEntity;
import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.LandmarkRepository;
import com.example.traveling_platform.repositories.TourPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class TourPlanService {
    @Autowired
    private TourPlanRepository tourPlanRepository;

    @Autowired
    private LandmarkRepository landmarkRepository;

    public List<TourPlanEntity> getAll() {
        return tourPlanRepository.findAll();
    }

    public TourPlanEntity getById(Long id) {
        return tourPlanRepository.findById(id).orElseThrow(() -> new ApiException("Tour plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
    }

    public TourPlanEntity create(TourPlanEntity newTourPlan) {
        return tourPlanRepository.save(newTourPlan);
    }

    public TourPlanEntity update(TourPlanUpdateDto tourPlan, Long id) {
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

    public ResponseEntity<Map<String, Object>> delete(Long id) {
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

    public TourPlanEntity deleteLandmark(List<Long> landmarkIds, Long tourPlanId) {
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
