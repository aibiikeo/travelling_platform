package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.TravelPlanDto;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.services.TravelPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/travel-plans")
public class TravelPlanController {

    @Autowired
    private TravelPlanService travelPlanService;

    @GetMapping("/get-all")
    public List<TravelPlanEntity> getAll() {
        return travelPlanService.getAll();
    }

    @GetMapping("/get/{id}")
    public TravelPlanEntity getById(@PathVariable("id") Long id) {
        return travelPlanService.getById(id);
    }

    @PostMapping("/create")
    public TravelPlanEntity create(@RequestBody TravelPlanDto newTravelPlan) {
        return travelPlanService.create(newTravelPlan);
    }

    @PutMapping("update/{id}")
    public TravelPlanEntity update(@RequestBody TravelPlanDto travelPlan, @PathVariable("id") Long id) {
        return travelPlanService.update(travelPlan, id);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        return travelPlanService.delete(id);
    }

    @DeleteMapping("delete-landmark/{id}")
    public TravelPlanEntity deleteLandmark(@RequestBody List<Long> landmarkIds, @PathVariable("id") Long travelPlanId) {
        return travelPlanService.deleteLandmark(landmarkIds, travelPlanId);
    }

}
