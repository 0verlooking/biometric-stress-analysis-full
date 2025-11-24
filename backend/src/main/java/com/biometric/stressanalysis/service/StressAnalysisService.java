package com.biometric.stressanalysis.service;

import com.biometric.stressanalysis.dto.StressAnalysisDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for StressAnalysis operations
 */
public interface StressAnalysisService {

    StressAnalysisDTO analyzeStress(Long biometricDataId);

    Optional<StressAnalysisDTO> getStressAnalysisById(Long id);

    List<StressAnalysisDTO> getStressAnalysisByUserId(Long userId);

    List<StressAnalysisDTO> getStressAnalysisByUserIdAndDateRange(
        Long userId,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    Double getAverageStressScore(Long userId, LocalDateTime startDate);

    void deleteStressAnalysis(Long id);

    Long countByUserId(Long userId);
}
