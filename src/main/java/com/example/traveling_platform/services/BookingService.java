package com.example.traveling_platform.services;

import com.example.traveling_platform.dto.BookingUpdateDto;
import com.example.traveling_platform.entities.BookingEntity;
import com.example.traveling_platform.entities.TourPlanEntity;
import com.example.traveling_platform.entities.TravelPlanEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.BookingRepository;
import com.example.traveling_platform.repositories.TourPlanRepository;
import com.example.traveling_platform.repositories.TravelPlanRepository;
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
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private TourPlanRepository tourPlanRepository;

    public List<BookingEntity> getAll() {
        return bookingRepository.findAll();
    }

    public BookingEntity getById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ApiException("Booking with id " + id + " not found", HttpStatusCode.valueOf(404)));
    }

    public BookingEntity create(BookingEntity newBooking) {
        return bookingRepository.save(newBooking);
    }

    public BookingEntity updateByBookingId(BookingUpdateDto booking, Long id) {
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

    public BookingEntity updateByUserId(BookingUpdateDto booking, Long id) {
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

    public ResponseEntity<Map<String, Object>> deleteByBookingId(Long id) {
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

    public ResponseEntity<Map<String, Object>> deleteByUserId(Long id) {
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

    public BookingEntity deleteTourPlan(List<Long> tourPlanIds, Long bookingId) {
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

    public BookingEntity deleteTravelPlan(List<Long> travelPlanIds, Long bookingId) {
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
