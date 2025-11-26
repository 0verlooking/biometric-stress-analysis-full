package com.biometric.stressanalysis.dto;

import lombok.*;

/**
 * DTO for average stress score response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageStressScoreDTO {
    private Long userId;
    private Double averageScore;
    private Long totalAnalyses;
}
