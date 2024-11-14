package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.SuccessDto;
import com.example.traveling_platform.dto.TourPlanUpdateDto;
import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.TourPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tour-plans")
public class TourPlanController {
    @Autowired
    private TourPlanRepository tourPlanRepository;

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
        return tourPlanRepository.save(toUpdate);
    }

    @DeleteMapping("delete/{id}")
    public SuccessDto delete (@PathVariable Long id) {
        TourPlanEntity tourPlan = tourPlanRepository.findById(id).orElseThrow(() -> new ApiException("Tour plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
        tourPlanRepository.delete(tourPlan);
        return new SuccessDto(true);
    }
}
