package com.example.traveling_platform.services;

import com.example.traveling_platform.dto.TourPlanDto;
import com.example.traveling_platform.entities.LandmarkEntity;
import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.entities.UserEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.mapper.TourPlanMapper;
import com.example.traveling_platform.repositories.LandmarkRepository;
import com.example.traveling_platform.repositories.TourPlanRepository;
import com.example.traveling_platform.repositories.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourPlanMapper tourPlanMapper;

    public List<TourPlanEntity> getAll() {
        return tourPlanRepository.findAll();
    }

    public TourPlanEntity getById(Long id) {
        return tourPlanRepository.findById(id).orElseThrow(() -> new ApiException("Tour plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
    }

    public TourPlanEntity create(TourPlanDto newTourPlanDto) {
        TourPlanEntity tourPlanEntity = tourPlanMapper.tourPlanDtoToTourPlan(newTourPlanDto);
        List<Long> requestedLandmarkIds = newTourPlanDto.getLandmarkIds();
        List<LandmarkEntity> landmarks = landmarkRepository.findAllById(requestedLandmarkIds);
        List<Long> foundLandmarkIds = landmarks.stream().map(LandmarkEntity::getId).toList();
        List<Long> missingIds = requestedLandmarkIds.stream().filter(id -> !foundLandmarkIds.contains(id)).toList();

        if (!missingIds.isEmpty()) {
            throw new ApiException("Landmarks with id " + missingIds + " is not found", HttpStatusCode.valueOf(404));
        }
        tourPlanEntity.setLandmarks(landmarks);
        tourPlanEntity.updatePrice();
        return tourPlanRepository.save(tourPlanEntity);
    }

    public TourPlanEntity update(TourPlanDto tourPlan, Long id) {
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
        if (tourPlan.getLandmarkIds() != null && !tourPlan.getLandmarkIds().isEmpty()) {
            List<LandmarkEntity> currentLandmarks = toUpdate.getLandmarks();
            List<LandmarkEntity> newLandmarks = landmarkRepository.findAllById(tourPlan.getLandmarkIds());
            for (LandmarkEntity newLandmark : newLandmarks) {
                if (!currentLandmarks.contains(newLandmark)) {
                    currentLandmarks.add(newLandmark);
                }
            }
            toUpdate.setLandmarks(currentLandmarks);
            toUpdate.updatePrice();
        }
        return tourPlanRepository.save(toUpdate);
    }

    public ResponseEntity<Map<String, Object>> delete(Long id) {
        Optional<TourPlanEntity> tourPlan = tourPlanRepository.findById(id);
        if (tourPlan.isPresent()) {
            TourPlanEntity tourPlanEntity = tourPlan.get();
            List<UserEntity> users = userRepository.findAll();
            users.forEach(user -> user.getTourPlan().remove(tourPlanEntity));
            userRepository.saveAll(users);
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
