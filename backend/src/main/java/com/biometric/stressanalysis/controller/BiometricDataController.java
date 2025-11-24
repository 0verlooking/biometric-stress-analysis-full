package com.biometric.stressanalysis.controller;

import com.biometric.stressanalysis.dto.BiometricDataDTO;
import com.biometric.stressanalysis.service.BiometricDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for biometric data operations
 */
@RestController
@RequestMapping("/api/biometric-data")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Biometric Data", description = "Biometric data management APIs")
public class BiometricDataController {

    private final BiometricDataService biometricDataService;

    @PostMapping
    @Operation(summary = "Create new biometric data entry")
    public ResponseEntity<BiometricDataDTO> createBiometricData(@Valid @RequestBody BiometricDataDTO dto) {
        BiometricDataDTO created = biometricDataService.createBiometricData(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get biometric data by ID")
    public ResponseEntity<BiometricDataDTO> getBiometricDataById(@PathVariable Long id) {
        return biometricDataService.getBiometricDataById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all biometric data for a user")
    public ResponseEntity<List<BiometricDataDTO>> getBiometricDataByUserId(@PathVariable Long userId) {
        List<BiometricDataDTO> data = biometricDataService.getBiometricDataByUserId(userId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/user/{userId}/date-range")
    @Operation(summary = "Get biometric data for a user within date range")
    public ResponseEntity<List<BiometricDataDTO>> getBiometricDataByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BiometricDataDTO> data = biometricDataService.getBiometricDataByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/user/{userId}/latest")
    @Operation(summary = "Get latest biometric data for a user")
    public ResponseEntity<BiometricDataDTO> getLatestBiometricData(@PathVariable Long userId) {
        BiometricDataDTO data = biometricDataService.getLatestBiometricDataByUserId(userId);
        return ResponseEntity.ok(data);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update biometric data")
    public ResponseEntity<BiometricDataDTO> updateBiometricData(
            @PathVariable Long id,
            @Valid @RequestBody BiometricDataDTO dto) {
        BiometricDataDTO updated = biometricDataService.updateBiometricData(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete biometric data")
    public ResponseEntity<Void> deleteBiometricData(@PathVariable Long id) {
        biometricDataService.deleteBiometricData(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Get count of biometric data entries for a user")
    public ResponseEntity<Long> getCountByUserId(@PathVariable Long userId) {
        Long count = biometricDataService.countByUserId(userId);
        return ResponseEntity.ok(count);
    }
}
