package com.biometric.stressanalysis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Recommendation entity representing health recommendations
 * Based on stress analysis results
 */
@Entity
@Table(name = "recommendations", indexes = {
    @Index(name = "idx_stress_analysis", columnList = "stress_analysis_id"),
    @Index(name = "idx_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stress_analysis_id", nullable = false)
    @JsonIgnore
    private StressAnalysis stressAnalysis;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column
    private Boolean completed = false;

    public enum Category {
        PHYSICAL_ACTIVITY("Фізична активність"),
        NUTRITION("Харчування"),
        SLEEP("Сон"),
        RELAXATION("Релаксація"),
        MEDICAL("Медична консультація"),
        LIFESTYLE("Стиль життя");

        private final String description;

        Category(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
}
