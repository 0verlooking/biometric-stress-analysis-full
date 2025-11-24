package com.biometric.stressanalysis.repository;

import com.biometric.stressanalysis.entity.StressAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StressAnalysis entity
 */
@Repository
public interface StressAnalysisRepository extends JpaRepository<StressAnalysis, Long> {

    List<StressAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<StressAnalysis> findByBiometricDataId(Long biometricDataId);

    @Query("SELECT sa FROM StressAnalysis sa " +
           "LEFT JOIN FETCH sa.recommendations " +
           "WHERE sa.id = :id")
    Optional<StressAnalysis> findByIdWithRecommendations(@Param("id") Long id);

    @Query("SELECT sa FROM StressAnalysis sa " +
           "WHERE sa.user.id = :userId " +
           "AND sa.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY sa.createdAt DESC")
    List<StressAnalysis> findByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT AVG(sa.stressScore) FROM StressAnalysis sa " +
           "WHERE sa.user.id = :userId " +
           "AND sa.createdAt >= :startDate")
    Double calculateAverageStressScore(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate
    );

    Long countByUserId(Long userId);
}
