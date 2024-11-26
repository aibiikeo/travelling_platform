package com.example.traveling_platform.services;

import com.example.traveling_platform.dto.LandmarkDto;
import com.example.traveling_platform.entities.LandmarkEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.LandmarkRepository;
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
public class LandmarkService {
    @Autowired
    private LandmarkRepository landmarkRepository;

    public List<LandmarkEntity> getAll() {
        return landmarkRepository.findAll();
    }

    public LandmarkEntity getById(Long id) {
        return landmarkRepository.findById(id).orElseThrow(() -> new ApiException("Landmark with id " + id + " is not found", HttpStatusCode.valueOf(404)));
    }

    public LandmarkEntity create(LandmarkEntity newLandmark) {
        if (newLandmark.getPrice() == null || newLandmark.getPrice() <= 0) {
            throw new ApiException("landmark price should be greater than zero", HttpStatusCode.valueOf(400));
        }
        Optional<LandmarkEntity> existingLandmark = landmarkRepository.findByTitle(newLandmark.getTitle());
        if (existingLandmark.isPresent()) {
            throw new ApiException("Landmark with title '" + newLandmark.getTitle() + "' already exists", HttpStatusCode.valueOf(400));
        }
        return landmarkRepository.save(newLandmark);
    }

    public LandmarkEntity update(LandmarkDto landmark, Long id) {
        LandmarkEntity toUpdate = landmarkRepository.findById(id).orElseThrow(() -> new ApiException("Landmark with id " + id + " is not found", HttpStatusCode.valueOf(404)));
        if (landmark.getTitle() != null) {
            toUpdate.setTitle(landmark.getTitle());
        }
        if (landmark.getDescription() != null) {
            toUpdate.setDescription(landmark.getDescription());
        }
        if (landmark.getLocation() != null) {
            toUpdate.setLocation(landmark.getLocation());
        }
        if (landmark.getPrice() != null) {
            if (landmark.getPrice() <= 0) {
                throw new ApiException("landmark price must be greater than zero", HttpStatusCode.valueOf(400));
            }
            toUpdate.setPrice(landmark.getPrice());
        }
        if (landmark.getImageUrl() != null){
            toUpdate.setImageUrl(landmark.getImageUrl());
        }
        return landmarkRepository.save(toUpdate);
    }

    public ResponseEntity<Map<String, Object>> delete (Long id) {
        Optional<LandmarkEntity> landmark = landmarkRepository.findById(id);
        if (landmark.isPresent()) {
            landmarkRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Landmark with id " + id + " is deleted");
            response.put("deleted landmark", landmark.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else throw new ApiException("Landmark with id " + id + " is not found", HttpStatusCode.valueOf(404));
    }
}
