package com.example.traveling_platform.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String password;

    LocalDateTime createdAt;

    @PrePersist
    private void init() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(fetch = FetchType.EAGER)
    List<TravelPlanEntity> travelPlan;

    @ManyToMany(fetch = FetchType.EAGER)
    List<TourPlanEntity> tourPlan;
}
