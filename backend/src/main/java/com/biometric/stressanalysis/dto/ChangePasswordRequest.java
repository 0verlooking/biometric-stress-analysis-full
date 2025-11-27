package com.biometric.stressanalysis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for password change requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
}
