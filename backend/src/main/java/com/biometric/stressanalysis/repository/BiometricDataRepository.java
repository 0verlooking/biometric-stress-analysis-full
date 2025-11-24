package com.biometric.stressanalysis.repository;

import com.biometric.stressanalysis.entity.BiometricData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for BiometricData entity
 */
@Repository
public interface BiometricDataRepository extends JpaRepository<BiometricData, Long> {

    List<BiometricData> findByUserIdOrderByMeasurementTimeDesc(Long userId);

    List<BiometricData> findByUserIdAndMeasurementTimeBetween(
        Long userId,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    @Query("SELECT bd FROM BiometricData bd WHERE bd.user.id = :userId " +
           "AND bd.measurementTime >= :startTime " +
           "ORDER BY bd.measurementTime DESC")
    List<BiometricData> findRecentByUserId(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime
    );

    @Query("SELECT bd FROM BiometricData bd " +
           "WHERE bd.user.id = :userId " +
           "ORDER BY bd.measurementTime DESC " +
           "LIMIT 1")
    BiometricData findLatestByUserId(@Param("userId") Long userId);

    Long countByUserId(Long userId);
}
