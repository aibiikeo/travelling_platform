package com.example.traveling_platform.repositories;

import com.example.traveling_platform.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    Optional<BookingEntity> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookingEntity b WHERE b.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}
