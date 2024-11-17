package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.BookingUpdateDto;
import com.example.traveling_platform.entities.*;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.BookingRepository;
import com.example.traveling_platform.repositories.TourPlanRepository;
import com.example.traveling_platform.repositories.TravelPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private TourPlanRepository tourPlanRepository;

    @GetMapping("/get-all")
    public List<BookingEntity> getAll() {
        return bookingRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public BookingEntity getById(@PathVariable("id") Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ApiException("Booking with id " + id + " not found", HttpStatusCode.valueOf(404)));
    }

    @PostMapping("/create")
    public BookingEntity createBooking(@RequestBody BookingEntity newBooking) {
        return bookingRepository.save(newBooking);
    }

    @PutMapping("/update-by-booking-id/{id}")
    public BookingEntity updateByBookingId(@RequestBody BookingUpdateDto booking, @PathVariable("id") Long id) {
        BookingEntity toUpdate = bookingRepository.findById(id).orElseThrow(() -> new ApiException("Booking with id " + id + " not found", HttpStatusCode.valueOf(404)));
        if (booking.getTravelPlanIds() != null && !booking.getTravelPlanIds().isEmpty()) {
            List<TravelPlanEntity> currentTravelPlans = toUpdate.getTravelPlan();
            List<TravelPlanEntity> newTravelPlans = travelPlanRepository.findAllById(booking.getTravelPlanIds());
            for (TravelPlanEntity newTravelPlan : newTravelPlans) {
                if (!currentTravelPlans.contains(newTravelPlan)) {
                    currentTravelPlans.add(newTravelPlan);
                }
            }
            toUpdate.setTravelPlan(currentTravelPlans);
        }
        if (booking.getTourPlanIds() != null && !booking.getTourPlanIds().isEmpty()) {
            List<TourPlanEntity> currentTourPlans = toUpdate.getTourPlan();
            List<TourPlanEntity> newTourPlans = tourPlanRepository.findAllById(booking.getTourPlanIds());
            for (TourPlanEntity newTourPlan : newTourPlans) {
                if (!currentTourPlans.contains(newTourPlan)) {
                    currentTourPlans.add(newTourPlan);
                }
            }
            toUpdate.setTourPlan(currentTourPlans);
        }
        return bookingRepository.save(toUpdate);
    }

    @PutMapping("/update-by-user-id/{id}")
    public BookingEntity updateByUserId(@RequestBody BookingUpdateDto booking, @PathVariable("id") Long id) {
        BookingEntity toUpdate = bookingRepository.findByUserId(id).orElseThrow(() -> new ApiException("User with id " + id + " not found", HttpStatusCode.valueOf(404)));
        if (booking.getTravelPlanIds() != null && !booking.getTravelPlanIds().isEmpty()) {
            List<TravelPlanEntity> currentTravelPlans = toUpdate.getTravelPlan();
            List<TravelPlanEntity> newTravelPlans = travelPlanRepository.findAllById(booking.getTravelPlanIds());
            for (TravelPlanEntity newTravelPlan : newTravelPlans) {
                if (!currentTravelPlans.contains(newTravelPlan)) {
                    currentTravelPlans.add(newTravelPlan);
                }
            }
            toUpdate.setTravelPlan(currentTravelPlans);
        }
        if (booking.getTourPlanIds() != null && !booking.getTourPlanIds().isEmpty()) {
            List<TourPlanEntity> currentTourPlans = toUpdate.getTourPlan();
            List<TourPlanEntity> newTourPlans = tourPlanRepository.findAllById(booking.getTourPlanIds());
            for (TourPlanEntity newTourPlan : newTourPlans) {
                if (!currentTourPlans.contains(newTourPlan)) {
                    currentTourPlans.add(newTourPlan);
                }
            }
            toUpdate.setTourPlan(currentTourPlans);
        }
        return bookingRepository.save(toUpdate);
    }

    @DeleteMapping("delete-by-booking-id/{id}")
    public ResponseEntity<Map<String, Object>> deleteByBookingId(@PathVariable Long id) {
        Optional<BookingEntity> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            bookingRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking with id " + id + " is deleted");
            response.put("deleted booking", booking.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else throw new ApiException("Booking with id " + id + " is not found", HttpStatusCode.valueOf(404));
    }

    @DeleteMapping("delete-by-user-id/{id}")
    public ResponseEntity<Map<String, Object>> deleteByUserId(@PathVariable Long id) {
        Optional<BookingEntity> booking = bookingRepository.findByUserId(id);
        if (booking.isPresent()) {
            bookingRepository.deleteByUserId(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking of user with id " + id + " is deleted");
            response.put("deleted booking", booking.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else throw new ApiException("Booking of user with id " + id + " is not found", HttpStatusCode.valueOf(404));
    }

    @DeleteMapping("delete-tour-plan/{id}")
    public BookingEntity deleteTourPlan(@RequestBody List<Long> tourPlanIds, @PathVariable("id") Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ApiException("Booking with id " + bookingId + " is not found", HttpStatusCode.valueOf(404)));
        List<TourPlanEntity> currentTourPlans = booking.getTourPlan();
        for (Long tourPlanId : tourPlanIds){
            boolean exists = currentTourPlans.stream().anyMatch(tourPlan -> tourPlan.getId().equals(tourPlanId));
            if (!exists) throw new ApiException("Tour plan with id " + tourPlanId + " is not found in this booking", HttpStatusCode.valueOf(404));
        }
        currentTourPlans.removeIf(tourPlan -> tourPlanIds.contains(tourPlan.getId()));
        booking.setTourPlan(currentTourPlans);
        return bookingRepository.save(booking);
    }

    @DeleteMapping("delete-travel-plan/{id}")
        public BookingEntity deleteTravelPlan(@RequestBody List<Long> travelPlanIds, @PathVariable("id") Long bookingId) {
            BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ApiException("Booking with id " + bookingId + " is not found", HttpStatusCode.valueOf(404)));
            List<TravelPlanEntity> currentTravelPlans = booking.getTravelPlan();
            for (Long travelPlanId : travelPlanIds){
                boolean exists = currentTravelPlans.stream().anyMatch(travelPlan -> travelPlan.getId().equals(travelPlanId));
                if (!exists) throw new ApiException("Travel plan with id " + travelPlanId + " is not found in this booking", HttpStatusCode.valueOf(404));
            }
            currentTravelPlans.removeIf(travelPlan -> travelPlanIds.contains(travelPlan.getId()));
            booking.setTravelPlan(currentTravelPlans);
            return bookingRepository.save(booking);
        }

}
