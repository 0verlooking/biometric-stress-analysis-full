package com.biometric.stressanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses
 * Flattened structure to match frontend expectations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    // Flattened user fields (frontend expects these at top level)
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
