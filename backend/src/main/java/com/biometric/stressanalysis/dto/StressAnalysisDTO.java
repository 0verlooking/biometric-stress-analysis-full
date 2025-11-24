package com.biometric.stressanalysis.dto;

import com.biometric.stressanalysis.entity.StressAnalysis;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for StressAnalysis entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StressAnalysisDTO {
    private Long id;
    private Long userId;
    private Long biometricDataId;
    private StressAnalysis.StressLevel stressLevel;
    private Double stressScore;
    private Double cardiovascularScore;
    private Double thermalScore;
    private Double biochemicalScore;
    private Double sleepScore;
    private Double respiratoryScore;
    private String analysis;
    private LocalDateTime createdAt;
    private List<RecommendationDTO> recommendations;
}
