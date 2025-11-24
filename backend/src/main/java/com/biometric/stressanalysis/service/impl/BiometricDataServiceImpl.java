package com.biometric.stressanalysis.service.impl;

import com.biometric.stressanalysis.dto.BiometricDataDTO;
import com.biometric.stressanalysis.entity.BiometricData;
import com.biometric.stressanalysis.entity.User;
import com.biometric.stressanalysis.exception.ResourceNotFoundException;
import com.biometric.stressanalysis.repository.BiometricDataRepository;
import com.biometric.stressanalysis.service.BiometricDataService;
import com.biometric.stressanalysis.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BiometricDataService
 * Demonstrates Dependency Injection and Single Responsibility
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BiometricDataServiceImpl implements BiometricDataService {

    private final BiometricDataRepository biometricDataRepository;
    private final UserService userService;

    @Override
    public BiometricDataDTO createBiometricData(BiometricDataDTO dto) {
        log.info("Creating new biometric data for user ID: {}", dto.getUserId());

        User user = userService.getUserEntityById(dto.getUserId());

        BiometricData biometricData = BiometricData.builder()
                .user(user)
                .measurementTime(dto.getMeasurementTime() != null ? dto.getMeasurementTime() : LocalDateTime.now())
                .heartRate(dto.getHeartRate())
                .systolicPressure(dto.getSystolicPressure())
                .diastolicPressure(dto.getDiastolicPressure())
                .bodyTemperature(dto.getBodyTemperature())
                .cortisolLevel(dto.getCortisolLevel())
                .sleepHours(dto.getSleepHours())
                .sleepQuality(dto.getSleepQuality())
                .respiratoryRate(dto.getRespiratoryRate())
                .oxygenSaturation(dto.getOxygenSaturation())
                .activityLevel(dto.getActivityLevel())
                .notes(dto.getNotes())
                .build();

        biometricData = biometricDataRepository.save(biometricData);
        log.info("Biometric data created successfully with ID: {}", biometricData.getId());

        return convertToDTO(biometricData);
    }

    @Override
    public BiometricDataDTO updateBiometricData(Long id, BiometricDataDTO dto) {
        log.info("Updating biometric data with ID: {}", id);

        BiometricData biometricData = biometricDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Biometric data not found with ID: " + id));

        biometricData.setHeartRate(dto.getHeartRate());
        biometricData.setSystolicPressure(dto.getSystolicPressure());
        biometricData.setDiastolicPressure(dto.getDiastolicPressure());
        biometricData.setBodyTemperature(dto.getBodyTemperature());
        biometricData.setCortisolLevel(dto.getCortisolLevel());
        biometricData.setSleepHours(dto.getSleepHours());
        biometricData.setSleepQuality(dto.getSleepQuality());
        biometricData.setRespiratoryRate(dto.getRespiratoryRate());
        biometricData.setOxygenSaturation(dto.getOxygenSaturation());
        biometricData.setActivityLevel(dto.getActivityLevel());
        biometricData.setNotes(dto.getNotes());

        biometricData = biometricDataRepository.save(biometricData);
        log.info("Biometric data updated successfully: {}", id);

        return convertToDTO(biometricData);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BiometricDataDTO> getBiometricDataById(Long id) {
        log.debug("Getting biometric data by ID: {}", id);
        return biometricDataRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BiometricDataDTO> getBiometricDataByUserId(Long userId) {
        log.debug("Getting biometric data for user ID: {}", userId);
        return biometricDataRepository.findByUserIdOrderByMeasurementTimeDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BiometricDataDTO> getBiometricDataByUserIdAndDateRange(
            Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting biometric data for user ID: {} between {} and {}", userId, startDate, endDate);
        return biometricDataRepository.findByUserIdAndMeasurementTimeBetween(userId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BiometricDataDTO getLatestBiometricDataByUserId(Long userId) {
        log.debug("Getting latest biometric data for user ID: {}", userId);
        BiometricData latest = biometricDataRepository.findLatestByUserId(userId);
        if (latest == null) {
            throw new ResourceNotFoundException("No biometric data found for user ID: " + userId);
        }
        return convertToDTO(latest);
    }

    @Override
    public void deleteBiometricData(Long id) {
        log.info("Deleting biometric data with ID: {}", id);

        if (!biometricDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("Biometric data not found with ID: " + id);
        }

        biometricDataRepository.deleteById(id);
        log.info("Biometric data deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByUserId(Long userId) {
        return biometricDataRepository.countByUserId(userId);
    }

    private BiometricDataDTO convertToDTO(BiometricData biometricData) {
        return BiometricDataDTO.builder()
                .id(biometricData.getId())
                .userId(biometricData.getUser().getId())
                .measurementTime(biometricData.getMeasurementTime())
                .heartRate(biometricData.getHeartRate())
                .systolicPressure(biometricData.getSystolicPressure())
                .diastolicPressure(biometricData.getDiastolicPressure())
                .bodyTemperature(biometricData.getBodyTemperature())
                .cortisolLevel(biometricData.getCortisolLevel())
                .sleepHours(biometricData.getSleepHours())
                .sleepQuality(biometricData.getSleepQuality())
                .respiratoryRate(biometricData.getRespiratoryRate())
                .oxygenSaturation(biometricData.getOxygenSaturation())
                .activityLevel(biometricData.getActivityLevel())
                .notes(biometricData.getNotes())
                .build();
    }
}
