package com.biometric.stressanalysis.pattern;

import com.biometric.stressanalysis.entity.BiometricData;
import com.biometric.stressanalysis.entity.StressAnalysis;
import org.springframework.stereotype.Component;

/**
 * Standard implementation of StressAnalysisStrategy
 * Uses weighted scoring algorithm based on multiple biometric indicators
 */
@Component
public class StandardStressAnalysisStrategy implements StressAnalysisStrategy {

    // Weight constants for different indicators
    private static final double CARDIOVASCULAR_WEIGHT = 0.30;
    private static final double THERMAL_WEIGHT = 0.15;
    private static final double BIOCHEMICAL_WEIGHT = 0.25;
    private static final double SLEEP_WEIGHT = 0.20;
    private static final double RESPIRATORY_WEIGHT = 0.10;

    @Override
    public StressAnalysis analyze(BiometricData data) {
        // Calculate individual component scores
        double cardiovascularScore = calculateCardiovascularScore(data);
        double thermalScore = calculateThermalScore(data);
        double biochemicalScore = calculateBiochemicalScore(data);
        double sleepScore = calculateSleepScore(data);
        double respiratoryScore = calculateRespiratoryScore(data);

        // Calculate weighted total stress score
        double totalScore = (cardiovascularScore * CARDIOVASCULAR_WEIGHT) +
                           (thermalScore * THERMAL_WEIGHT) +
                           (biochemicalScore * BIOCHEMICAL_WEIGHT) +
                           (sleepScore * SLEEP_WEIGHT) +
                           (respiratoryScore * RESPIRATORY_WEIGHT);

        // Determine stress level
        StressAnalysis.StressLevel stressLevel = StressAnalysis.determineStressLevel(totalScore);

        // Generate analysis text
        String analysisText = generateAnalysisText(stressLevel, totalScore, data);

        // Build and return StressAnalysis entity
        return StressAnalysis.builder()
                .biometricData(data)
                .user(data.getUser())
                .stressScore(totalScore)
                .stressLevel(stressLevel)
                .cardiovascularScore(cardiovascularScore)
                .thermalScore(thermalScore)
                .biochemicalScore(biochemicalScore)
                .sleepScore(sleepScore)
                .respiratoryScore(respiratoryScore)
                .analysis(analysisText)
                .build();
    }

    private double calculateCardiovascularScore(BiometricData data) {
        double score = 0.0;
        int heartRate = data.getHeartRate();
        int systolic = data.getSystolicPressure();
        int diastolic = data.getDiastolicPressure();

        // Heart rate scoring (60-100 bpm is normal)
        if (heartRate < 60) {
            score += 10;
        } else if (heartRate <= 100) {
            score += 0;
        } else if (heartRate <= 120) {
            score += 30;
        } else {
            score += 60;
        }

        // Blood pressure scoring
        if (systolic < 120 && diastolic < 80) {
            score += 0;
        } else if (systolic <= 139 || diastolic <= 89) {
            score += 20;
        } else {
            score += 40;
        }

        return Math.min(score, 100);
    }

    private double calculateThermalScore(BiometricData data) {
        double temperature = data.getBodyTemperature();

        // Normal body temperature: 36.1 - 37.2°C
        if (temperature < 36.1) {
            return Math.abs(36.1 - temperature) * 30;
        } else if (temperature <= 37.2) {
            return 0;
        } else if (temperature <= 38.0) {
            return (temperature - 37.2) * 40;
        } else {
            return Math.min((temperature - 37.2) * 50, 100);
        }
    }

    private double calculateBiochemicalScore(BiometricData data) {
        if (data.getCortisolLevel() == null) {
            return 0;
        }

        double cortisolLevel = data.getCortisolLevel();

        // Normal cortisol: 140-690 nmol/L (morning)
        if (cortisolLevel < 140) {
            return 10;
        } else if (cortisolLevel <= 690) {
            return 0;
        } else if (cortisolLevel <= 1000) {
            return ((cortisolLevel - 690) / 310) * 50;
        } else {
            return Math.min(((cortisolLevel - 690) / 310) * 70, 100);
        }
    }

    private double calculateSleepScore(BiometricData data) {
        if (data.getSleepHours() == null) {
            return 0;
        }

        double sleepHours = data.getSleepHours();
        double baseScore = 0;

        // Optimal sleep: 7-9 hours
        if (sleepHours < 5) {
            baseScore = 70;
        } else if (sleepHours < 7) {
            baseScore = 30;
        } else if (sleepHours <= 9) {
            baseScore = 0;
        } else {
            baseScore = 20;
        }

        // Adjust based on sleep quality
        if (data.getSleepQuality() != null) {
            switch (data.getSleepQuality()) {
                case POOR -> baseScore += 30;
                case FAIR -> baseScore += 15;
                case GOOD -> baseScore += 0;
                case EXCELLENT -> baseScore -= 5;
            }
        }

        return Math.min(Math.max(baseScore, 0), 100);
    }

    private double calculateRespiratoryScore(BiometricData data) {
        double score = 0;

        // Respiratory rate (normal: 12-20 breaths/min)
        if (data.getRespiratoryRate() != null) {
            int respiratoryRate = data.getRespiratoryRate();
            if (respiratoryRate < 12) {
                score += 20;
            } else if (respiratoryRate <= 20) {
                score += 0;
            } else if (respiratoryRate <= 25) {
                score += 30;
            } else {
                score += 50;
            }
        }

        // Oxygen saturation (normal: 95-100%)
        if (data.getOxygenSaturation() != null) {
            double saturation = data.getOxygenSaturation();
            if (saturation >= 95) {
                score += 0;
            } else if (saturation >= 90) {
                score += 25;
            } else {
                score += 50;
            }
        }

        return Math.min(score, 100);
    }

    private String generateAnalysisText(StressAnalysis.StressLevel level, double score, BiometricData data) {
        StringBuilder analysis = new StringBuilder();

        analysis.append(String.format("Загальний рівень стресу: %.1f/100 - %s. ",
            score, level.getDescription()));

        if (score < 25) {
            analysis.append("Ваші біометричні показники знаходяться в нормі. ");
        } else if (score < 50) {
            analysis.append("Виявлено помірні ознаки стресу. ");
        } else if (score < 75) {
            analysis.append("Виявлено високий рівень стресу, рекомендується звернути увагу на здоров'я. ");
        } else {
            analysis.append("УВАГА: Критично високий рівень стресу! Рекомендується консультація лікаря. ");
        }

        // Add specific concerns
        if (data.getHeartRate() > 100) {
            analysis.append("Підвищений пульс. ");
        }
        if (data.getSystolicPressure() > 140) {
            analysis.append("Підвищений тиск. ");
        }
        if (data.getSleepHours() != null && data.getSleepHours() < 7) {
            analysis.append("Недостатня тривалість сну. ");
        }

        return analysis.toString();
    }

    @Override
    public String getStrategyName() {
        return "Standard Weighted Analysis";
    }
}
