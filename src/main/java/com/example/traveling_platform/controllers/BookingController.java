package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.BookingUpdateDto;
import com.example.traveling_platform.entities.BookingEntity;
import com.example.traveling_platform.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/get-all")
    public List<BookingEntity> getAll() {
        return bookingService.getAll();
    }

    @GetMapping("/get/{id}")
    public BookingEntity getById(@PathVariable("id") Long id) {
        return bookingService.getById(id);
    }

    @PostMapping("/create")
    public BookingEntity create(@RequestBody BookingEntity newBooking) {
        return bookingService.create(newBooking);
    }

    @PutMapping("/update-by-booking-id/{id}")
    public BookingEntity updateByBookingId(@RequestBody BookingUpdateDto booking, @PathVariable("id") Long id) {
        return bookingService.updateByBookingId(booking, id);
    }

    @PutMapping("/update-by-user-id/{id}")
    public BookingEntity updateByUserId(@RequestBody BookingUpdateDto booking, @PathVariable("id") Long id) {
        return bookingService.updateByUserId(booking, id);
    }

    @DeleteMapping("delete-by-booking-id/{id}")
    public ResponseEntity<Map<String, Object>> deleteByBookingId(@PathVariable Long id) {
        return bookingService.deleteByBookingId(id);
    }

    @DeleteMapping("delete-by-user-id/{id}")
    public ResponseEntity<Map<String, Object>> deleteByUserId(@PathVariable Long id) {
        return bookingService.deleteByBookingId(id);
    }

    @DeleteMapping("delete-tour-plan/{id}")
    public BookingEntity deleteTourPlan(@RequestBody List<Long> tourPlanIds, @PathVariable("id") Long bookingId) {
        return bookingService.deleteTourPlan(tourPlanIds, bookingId);
    }

    @DeleteMapping("delete-travel-plan/{id}")
    public BookingEntity deleteTravelPlan(@RequestBody List<Long> travelPlanIds, @PathVariable("id") Long bookingId) {
        return bookingService.deleteTravelPlan(travelPlanIds, bookingId);
    }

}
