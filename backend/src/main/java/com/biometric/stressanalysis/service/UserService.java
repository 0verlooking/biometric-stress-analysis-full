package com.biometric.stressanalysis.service;

import com.biometric.stressanalysis.dto.RegisterRequest;
import com.biometric.stressanalysis.dto.UserDTO;
import com.biometric.stressanalysis.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations
 * Follows Interface Segregation Principle (SOLID)
 */
public interface UserService {

    UserDTO createUser(RegisterRequest request);

    UserDTO updateUser(Long id, UserDTO userDTO);

    Optional<UserDTO> getUserById(Long id);

    Optional<UserDTO> getUserByUsername(String username);

    List<UserDTO> getAllUsers();

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User getUserEntityById(Long id);
}
