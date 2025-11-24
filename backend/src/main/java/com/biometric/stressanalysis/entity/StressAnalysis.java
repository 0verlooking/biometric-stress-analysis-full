package com.biometric.stressanalysis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * StressAnalysis entity representing the analysis results of biometric data
 * Contains stress level calculations and recommendations
 */
@Entity
@Table(name = "stress_analysis", indexes = {
    @Index(name = "idx_user_stress", columnList = "user_id, stress_level"),
    @Index(name = "idx_analysis_date", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StressAnalysis extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biometric_data_id", nullable = false)
    @JsonIgnore
    private BiometricData biometricData;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StressLevel stressLevel;

    // Stress score from 0 to 100
    @Column(nullable = false)
    private Double stressScore;

    // Individual component scores
    @Column
    private Double cardiovascularScore;

    @Column
    private Double thermalScore;

    @Column
    private Double biochemicalScore;

    @Column
    private Double sleepScore;

    @Column
    private Double respiratoryScore;

    @Column(length = 1000)
    private String analysis;

    @OneToMany(mappedBy = "stressAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Recommendation> recommendations = new ArrayList<>();

    public enum StressLevel {
        LOW("Низький рівень стресу"),
        MODERATE("Помірний рівень стресу"),
        HIGH("Високий рівень стресу"),
        CRITICAL("Критичний рівень стресу");

        private final String description;

        StressLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public void addRecommendation(Recommendation recommendation) {
        recommendations.add(recommendation);
        recommendation.setStressAnalysis(this);
    }

    /**
     * Determines stress level based on stress score
     * Factory Method pattern
     */
    public static StressLevel determineStressLevel(double stressScore) {
        if (stressScore < 25) {
            return StressLevel.LOW;
        } else if (stressScore < 50) {
            return StressLevel.MODERATE;
        } else if (stressScore < 75) {
            return StressLevel.HIGH;
        } else {
            return StressLevel.CRITICAL;
        }
    }
}
