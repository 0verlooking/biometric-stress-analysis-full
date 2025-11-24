package com.biometric.stressanalysis.dto;

import com.biometric.stressanalysis.entity.BiometricData;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for BiometricData entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiometricDataDTO {
    private Long id;
    private Long userId;
    private LocalDateTime measurementTime;
    private Integer heartRate;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private Double bodyTemperature;
    private Double cortisolLevel;
    private Double sleepHours;
    private BiometricData.SleepQuality sleepQuality;
    private Integer respiratoryRate;
    private Double oxygenSaturation;
    private Integer activityLevel;
    private String notes;
}
