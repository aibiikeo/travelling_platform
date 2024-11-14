package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.SuccessDto;
import com.example.traveling_platform.dto.TravelPlanUpdateDto;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.TravelPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-plans")
public class TravelPlanController {
    @Autowired
    private TravelPlanRepository travelPlanRepository;

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
        return travelPlanRepository.save(toUpdate);
    }

    @DeleteMapping("delete/{id}")
    public SuccessDto delete (@PathVariable Long id) {
        TravelPlanEntity travelPlan = travelPlanRepository.findById(id).orElseThrow(() -> new ApiException("Travel plan with id " + id + " is not found", HttpStatusCode.valueOf(404)));
        travelPlanRepository.delete(travelPlan);
        return new SuccessDto(true);
    }
}
