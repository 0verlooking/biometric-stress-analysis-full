package com.biometric.stressanalysis.service;

import com.biometric.stressanalysis.dto.BiometricDataDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for BiometricData operations
 */
public interface BiometricDataService {

    BiometricDataDTO createBiometricData(BiometricDataDTO biometricDataDTO);

    BiometricDataDTO updateBiometricData(Long id, BiometricDataDTO biometricDataDTO);

    Optional<BiometricDataDTO> getBiometricDataById(Long id);

    List<BiometricDataDTO> getBiometricDataByUserId(Long userId);

    List<BiometricDataDTO> getBiometricDataByUserIdAndDateRange(
        Long userId,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    BiometricDataDTO getLatestBiometricDataByUserId(Long userId);

    void deleteBiometricData(Long id);

    Long countByUserId(Long userId);
}
