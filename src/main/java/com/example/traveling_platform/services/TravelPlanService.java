package com.example.traveling_platform.services;

import com.example.traveling_platform.dto.TravelPlanDto;
import com.example.traveling_platform.entities.LandmarkEntity;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.entities.UserEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.mapper.TravelPlanMapper;
import com.example.traveling_platform.repositories.LandmarkRepository;
import com.example.traveling_platform.repositories.TravelPlanRepository;
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
public class TravelPlanService {
    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private LandmarkRepository landmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TravelPlanMapper travelPlanMapper;

    public List<TravelPlanEntity> getAll() {
        return travelPlanRepository.findAll();
    }

    public TravelPlanEntity getById(Long id) {
        return travelPlanRepository.findById(id).orElseThrow(() -> new ApiException("Travel plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
    }

    public TravelPlanEntity create(TravelPlanEntity newTravelPlan) {
        return travelPlanRepository.save(newTravelPlan);
    }

    public TravelPlanEntity create(TravelPlanDto newTravelPlanDto) {
        TravelPlanEntity travelPlanEntity = travelPlanMapper.travelPlanDtoToTravelPlan(newTravelPlanDto);
        List<Long> requestedLandmarkIds = newTravelPlanDto.getLandmarkIds();
        List<LandmarkEntity> landmarks = landmarkRepository.findAllById(requestedLandmarkIds);
        List<Long> foundLandmarkIds = landmarks.stream().map(LandmarkEntity::getId).toList();
        List<Long> missingIds = requestedLandmarkIds.stream().filter(id -> !foundLandmarkIds.contains(id)).toList();

        if (!missingIds.isEmpty()) {
            throw new ApiException("Landmarks with id " + missingIds + " is not found", HttpStatusCode.valueOf(404));
        }
        travelPlanEntity.setLandmarks(landmarks);
        travelPlanEntity.updatePrice();
        return travelPlanRepository.save(travelPlanEntity);
    }

    public TravelPlanEntity update(TravelPlanDto travelPlan, Long id) {
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
        if (travelPlan.getLandmarkIds() != null && !travelPlan.getLandmarkIds().isEmpty()) {
            List<LandmarkEntity> currentLandmarks = toUpdate.getLandmarks();
            List<LandmarkEntity> newLandmarks = landmarkRepository.findAllById(travelPlan.getLandmarkIds());
            for (LandmarkEntity newLandmark : newLandmarks) {
                if (!currentLandmarks.contains(newLandmark)) {
                    currentLandmarks.add(newLandmark);
                }
            }
            toUpdate.setLandmarks(currentLandmarks);
            toUpdate.updatePrice();
        }
        return travelPlanRepository.save(toUpdate);
    }

    public ResponseEntity<Map<String, Object>> delete(Long id) {
        Optional<TravelPlanEntity> travelPlan = travelPlanRepository.findById(id);
        if (travelPlan.isPresent()) {
            TravelPlanEntity travelPlanEntity = travelPlan.get();
            List<UserEntity> users = userRepository.findAll();
            users.forEach(user -> user.getTravelPlan().remove(travelPlanEntity));
            userRepository.saveAll(users);
            travelPlanRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Travel plan with id " + id + " is deleted");
            response.put("deleted travel plan", travelPlan.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else throw new ApiException("Travel plan with id " + id + " is not found", HttpStatusCode.valueOf(404));
    }

    public TravelPlanEntity deleteLandmark(List<Long> landmarkIds, Long travelPlanId) {
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
