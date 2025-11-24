package com.biometric.stressanalysis.pattern;

import com.biometric.stressanalysis.entity.BiometricData;
import com.biometric.stressanalysis.entity.StressAnalysis;

/**
 * Strategy Pattern interface for stress analysis algorithms
 * Allows different analysis strategies to be implemented
 */
public interface StressAnalysisStrategy {

    /**
     * Analyzes biometric data and returns stress analysis result
     * @param biometricData the biometric data to analyze
     * @return stress analysis result
     */
    StressAnalysis analyze(BiometricData biometricData);

    /**
     * Returns the name of the strategy
     */
    String getStrategyName();
}
