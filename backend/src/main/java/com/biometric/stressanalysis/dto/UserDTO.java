package com.biometric.stressanalysis.dto;

import com.biometric.stressanalysis.entity.User;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for User entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private User.Gender gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String phoneNumber;
    private User.Role role;
    private Boolean active;
}
