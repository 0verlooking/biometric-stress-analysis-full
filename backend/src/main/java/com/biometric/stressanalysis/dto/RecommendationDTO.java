package com.biometric.stressanalysis.dto;

import com.biometric.stressanalysis.entity.Recommendation;
import lombok.*;

/**
 * DTO for Recommendation entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private Long id;
    private Long stressAnalysisId;
    private Recommendation.Category category;
    private String title;
    private String description;
    private Recommendation.Priority priority;
    private Boolean completed;
}
