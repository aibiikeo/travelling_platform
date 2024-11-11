package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dtos.LandmarkUpdateDto;
import com.example.traveling_platform.entities.LandmarkEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.LandmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/landmarks")
public class LandmarkController {

    @Autowired
    private LandmarkRepository landmarkRepository;

    @GetMapping("/get-all")
    public List<LandmarkEntity> getAll() {
        return landmarkRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public LandmarkEntity getById(@PathVariable("id") Long id) {
        return landmarkRepository.findById(id).orElseThrow(() -> new ApiException("Landmark with id " + id + " is not found", HttpStatusCode.valueOf(404)));
    }

    @PostMapping("/create")
    public LandmarkEntity create(@RequestBody LandmarkEntity newLandmark) {
        return landmarkRepository.save(newLandmark);
    }


    @PutMapping("update/{id}")
    public LandmarkEntity update(@RequestBody LandmarkUpdateDto landmark, @PathVariable("id") Long id) {
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
        if (landmark.getImageUrl() != null){
            toUpdate.setImageUrl(landmark.getImageUrl());
        }
        return landmarkRepository.save(toUpdate);
    }

    @DeleteMapping("delete/{id}")
    public SuccessDto delete (@PathVariable Long id) {
        LandmarkEntity landmark = landmarkRepository.findById(id).orElseThrow(() -> new ApiException("Landmark with id " + id + " is not found", HttpStatusCode.valueOf(404)));
        landmarkRepository.delete(landmark);
        return new SuccessDto(true);
    }

}
