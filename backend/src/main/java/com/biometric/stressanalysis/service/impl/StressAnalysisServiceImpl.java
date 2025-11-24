package com.biometric.stressanalysis.service.impl;

import com.biometric.stressanalysis.dto.RecommendationDTO;
import com.biometric.stressanalysis.dto.StressAnalysisDTO;
import com.biometric.stressanalysis.entity.BiometricData;
import com.biometric.stressanalysis.entity.Recommendation;
import com.biometric.stressanalysis.entity.StressAnalysis;
import com.biometric.stressanalysis.exception.ResourceNotFoundException;
import com.biometric.stressanalysis.pattern.RecommendationFactory;
import com.biometric.stressanalysis.pattern.StressAnalysisStrategy;
import com.biometric.stressanalysis.repository.BiometricDataRepository;
import com.biometric.stressanalysis.repository.StressAnalysisRepository;
import com.biometric.stressanalysis.service.StressAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of StressAnalysisService
 * Demonstrates Strategy Pattern and Factory Pattern usage
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StressAnalysisServiceImpl implements StressAnalysisService {

    private final StressAnalysisRepository stressAnalysisRepository;
    private final BiometricDataRepository biometricDataRepository;
    private final StressAnalysisStrategy analysisStrategy;
    private final RecommendationFactory recommendationFactory;

    @Override
    public StressAnalysisDTO analyzeStress(Long biometricDataId) {
        log.info("Starting stress analysis for biometric data ID: {}", biometricDataId);

        BiometricData biometricData = biometricDataRepository.findById(biometricDataId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Biometric data not found with ID: " + biometricDataId));

        // Use Strategy Pattern to analyze stress
        StressAnalysis analysis = analysisStrategy.analyze(biometricData);

        // Use Factory Pattern to create recommendations
        List<Recommendation> recommendations = recommendationFactory.createRecommendations(analysis);

        // Add recommendations to analysis
        recommendations.forEach(analysis::addRecommendation);

        // Save the analysis
        analysis = stressAnalysisRepository.save(analysis);

        log.info("Stress analysis completed with ID: {}, Stress Level: {}, Score: {}",
                analysis.getId(), analysis.getStressLevel(), analysis.getStressScore());

        return convertToDTO(analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StressAnalysisDTO> getStressAnalysisById(Long id) {
        log.debug("Getting stress analysis by ID: {}", id);
        return stressAnalysisRepository.findByIdWithRecommendations(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StressAnalysisDTO> getStressAnalysisByUserId(Long userId) {
        log.debug("Getting stress analyses for user ID: {}", userId);
        return stressAnalysisRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StressAnalysisDTO> getStressAnalysisByUserIdAndDateRange(
            Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting stress analyses for user ID: {} between {} and {}", userId, startDate, endDate);
        return stressAnalysisRepository.findByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageStressScore(Long userId, LocalDateTime startDate) {
        log.debug("Calculating average stress score for user ID: {} from {}", userId, startDate);
        Double average = stressAnalysisRepository.calculateAverageStressScore(userId, startDate);
        return average != null ? average : 0.0;
    }

    @Override
    public void deleteStressAnalysis(Long id) {
        log.info("Deleting stress analysis with ID: {}", id);

        if (!stressAnalysisRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stress analysis not found with ID: " + id);
        }

        stressAnalysisRepository.deleteById(id);
        log.info("Stress analysis deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByUserId(Long userId) {
        return stressAnalysisRepository.countByUserId(userId);
    }

    private StressAnalysisDTO convertToDTO(StressAnalysis analysis) {
        List<RecommendationDTO> recommendationDTOs = null;
        if (analysis.getRecommendations() != null) {
            recommendationDTOs = analysis.getRecommendations().stream()
                    .map(this::convertRecommendationToDTO)
                    .collect(Collectors.toList());
        }

        return StressAnalysisDTO.builder()
                .id(analysis.getId())
                .userId(analysis.getUser().getId())
                .biometricDataId(analysis.getBiometricData().getId())
                .stressLevel(analysis.getStressLevel())
                .stressScore(analysis.getStressScore())
                .cardiovascularScore(analysis.getCardiovascularScore())
                .thermalScore(analysis.getThermalScore())
                .biochemicalScore(analysis.getBiochemicalScore())
                .sleepScore(analysis.getSleepScore())
                .respiratoryScore(analysis.getRespiratoryScore())
                .analysis(analysis.getAnalysis())
                .createdAt(analysis.getCreatedAt())
                .recommendations(recommendationDTOs)
                .build();
    }

    private RecommendationDTO convertRecommendationToDTO(Recommendation recommendation) {
        return RecommendationDTO.builder()
                .id(recommendation.getId())
                .stressAnalysisId(recommendation.getStressAnalysis().getId())
                .category(recommendation.getCategory())
                .title(recommendation.getTitle())
                .description(recommendation.getDescription())
                .priority(recommendation.getPriority())
                .completed(recommendation.getCompleted())
                .build();
    }
}
