package com.biometric.stressanalysis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * BiometricData entity representing user's biometric measurements
 * Contains various health and stress indicators
 */
@Entity
@Table(name = "biometric_data", indexes = {
    @Index(name = "idx_user_measurement", columnList = "user_id, measurement_time"),
    @Index(name = "idx_measurement_time", columnList = "measurement_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiometricData extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private LocalDateTime measurementTime;

    // Heart Rate (beats per minute)
    @Column(nullable = false)
    private Integer heartRate;

    // Blood Pressure (systolic/diastolic in mmHg)
    @Column(nullable = false)
    private Integer systolicPressure;

    @Column(nullable = false)
    private Integer diastolicPressure;

    // Body Temperature (in Celsius)
    @Column(nullable = false)
    private Double bodyTemperature;

    // Cortisol Level (stress hormone, in nmol/L)
    @Column
    private Double cortisolLevel;

    // Sleep Quality (hours of sleep)
    @Column
    private Double sleepHours;

    @Column
    @Enumerated(EnumType.STRING)
    private SleepQuality sleepQuality;

    // Respiratory Rate (breaths per minute)
    @Column
    private Integer respiratoryRate;

    // Oxygen Saturation (percentage)
    @Column
    private Double oxygenSaturation;

    // Activity Level (steps or minutes of activity)
    @Column
    private Integer activityLevel;

    // Additional notes
    @Column(length = 500)
    private String notes;

    @OneToOne(mappedBy = "biometricData", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private StressAnalysis stressAnalysis;

    public enum SleepQuality {
        POOR, FAIR, GOOD, EXCELLENT
    }
}
