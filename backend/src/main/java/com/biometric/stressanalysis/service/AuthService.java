package com.biometric.stressanalysis.service;

import com.biometric.stressanalysis.dto.AuthRequest;
import com.biometric.stressanalysis.dto.AuthResponse;
import com.biometric.stressanalysis.dto.RegisterRequest;

/**
 * Service interface for authentication operations
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);
}
