package com.biometric.stressanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Biometric Stress Analysis System
 * Demonstrates SOLID principles and design patterns
 */
@SpringBootApplication
@EnableJpaAuditing
public class BiometricStressAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiometricStressAnalysisApplication.class, args);
    }
}
