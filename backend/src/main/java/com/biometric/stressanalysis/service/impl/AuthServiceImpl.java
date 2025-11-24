package com.biometric.stressanalysis.service.impl;

import com.biometric.stressanalysis.dto.AuthRequest;
import com.biometric.stressanalysis.dto.AuthResponse;
import com.biometric.stressanalysis.dto.RegisterRequest;
import com.biometric.stressanalysis.dto.UserDTO;
import com.biometric.stressanalysis.security.JwtUtil;
import com.biometric.stressanalysis.service.AuthService;
import com.biometric.stressanalysis.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuthService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Validate that username and email are unique
        if (userService.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create user
        UserDTO userDTO = userService.createUser(request);

        // Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        log.info("User registered successfully: {}", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Generate JWT token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        // Get user details
        UserDTO userDTO = userService.getUserByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        log.info("User logged in successfully: {}", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
    }
}
