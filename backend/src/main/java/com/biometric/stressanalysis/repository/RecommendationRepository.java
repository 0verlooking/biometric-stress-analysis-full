package com.biometric.stressanalysis.repository;

import com.biometric.stressanalysis.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Recommendation entity
 */
@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByStressAnalysisId(Long stressAnalysisId);

    List<Recommendation> findByStressAnalysisIdAndCompleted(Long stressAnalysisId, Boolean completed);

    @Query("SELECT r FROM Recommendation r " +
           "JOIN r.stressAnalysis sa " +
           "WHERE sa.user.id = :userId " +
           "AND r.completed = false " +
           "ORDER BY r.priority DESC, r.createdAt DESC")
    List<Recommendation> findActiveRecommendationsByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Recommendation r " +
           "JOIN r.stressAnalysis sa " +
           "WHERE sa.user.id = :userId " +
           "AND r.category = :category " +
           "ORDER BY r.createdAt DESC")
    List<Recommendation> findByUserIdAndCategory(
        @Param("userId") Long userId,
        @Param("category") Recommendation.Category category
    );

    Long countByStressAnalysisUserIdAndCompleted(Long userId, Boolean completed);
}
