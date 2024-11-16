package com.example.traveling_platform.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    UserEntity user;

    @ManyToMany(fetch = FetchType.EAGER)
    List<TravelPlanEntity> travelPlan;

    @ManyToMany(fetch = FetchType.EAGER)
    List<TourPlanEntity> tourPlan;
}
