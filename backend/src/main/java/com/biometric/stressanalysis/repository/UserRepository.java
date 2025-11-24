package com.biometric.stressanalysis.repository;

import com.biometric.stressanalysis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity
 * Follows Repository pattern
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.biometricDataList WHERE u.id = :id")
    Optional<User> findByIdWithBiometricData(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.stressAnalysisList WHERE u.id = :id")
    Optional<User> findByIdWithStressAnalysis(@Param("id") Long id);
}
