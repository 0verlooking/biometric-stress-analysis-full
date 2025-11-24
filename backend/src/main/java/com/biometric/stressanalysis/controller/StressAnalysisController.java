package com.biometric.stressanalysis.controller;

import com.biometric.stressanalysis.dto.StressAnalysisDTO;
import com.biometric.stressanalysis.service.StressAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for stress analysis operations
 */
@RestController
@RequestMapping("/api/stress-analysis")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Stress Analysis", description = "Stress analysis management APIs")
public class StressAnalysisController {

    private final StressAnalysisService stressAnalysisService;

    @PostMapping("/analyze/{biometricDataId}")
    @Operation(summary = "Analyze stress from biometric data")
    public ResponseEntity<StressAnalysisDTO> analyzeStress(@PathVariable Long biometricDataId) {
        StressAnalysisDTO analysis = stressAnalysisService.analyzeStress(biometricDataId);
        return new ResponseEntity<>(analysis, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stress analysis by ID")
    public ResponseEntity<StressAnalysisDTO> getStressAnalysisById(@PathVariable Long id) {
        return stressAnalysisService.getStressAnalysisById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all stress analyses for a user")
    public ResponseEntity<List<StressAnalysisDTO>> getStressAnalysisByUserId(@PathVariable Long userId) {
        List<StressAnalysisDTO> analyses = stressAnalysisService.getStressAnalysisByUserId(userId);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/user/{userId}/date-range")
    @Operation(summary = "Get stress analyses for a user within date range")
    public ResponseEntity<List<StressAnalysisDTO>> getStressAnalysisByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<StressAnalysisDTO> analyses = stressAnalysisService.getStressAnalysisByUserIdAndDateRange(
                userId, startDate, endDate);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/user/{userId}/average-score")
    @Operation(summary = "Get average stress score for a user")
    public ResponseEntity<Double> getAverageStressScore(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        Double average = stressAnalysisService.getAverageStressScore(userId, startDate);
        return ResponseEntity.ok(average);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete stress analysis")
    public ResponseEntity<Void> deleteStressAnalysis(@PathVariable Long id) {
        stressAnalysisService.deleteStressAnalysis(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Get count of stress analyses for a user")
    public ResponseEntity<Long> getCountByUserId(@PathVariable Long userId) {
        Long count = stressAnalysisService.countByUserId(userId);
        return ResponseEntity.ok(count);
    }
}
