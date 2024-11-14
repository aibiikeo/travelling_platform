package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.BookingUpdateDto;
import com.example.traveling_platform.dto.SuccessDto;
import com.example.traveling_platform.entities.BookingEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

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

    @PutMapping("/update/{id}")
    public BookingEntity update(@RequestBody BookingUpdateDto booking, @PathVariable("id") Long id) {
        BookingEntity toUpdate = bookingRepository.findById(id).orElseThrow(() -> new ApiException("Booking with id " + id + " not found", HttpStatusCode.valueOf(404)));
        if (booking.getUser() != null) {
            toUpdate.setUser(booking.getUser());
        }
        if (booking.getTravelPlan() != null) {
            toUpdate.setTravelPlan(booking.getTravelPlan());
        }
        if (booking.getTourPlan() != null) {
            toUpdate.setTourPlan(booking.getTourPlan());
        }

        return bookingRepository.save(toUpdate);
    }

    @DeleteMapping("delete/{id}")
    public SuccessDto delete(@PathVariable Long id) {
        BookingEntity booking = bookingRepository.findById(id).orElseThrow(() -> new ApiException("Booking with id " + id + " is not found", HttpStatusCode.valueOf(404)));
        bookingRepository.delete(booking);
        return new SuccessDto(true);
    }
}
