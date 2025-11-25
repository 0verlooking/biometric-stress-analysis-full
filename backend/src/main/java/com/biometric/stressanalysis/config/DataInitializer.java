package com.biometric.stressanalysis.config;

import com.biometric.stressanalysis.entity.User;
import com.biometric.stressanalysis.entity.User.Gender;
import com.biometric.stressanalysis.entity.User.Role;
import com.biometric.stressanalysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Initializes database with test users
 * Creates users with properly encoded passwords
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");

        // Don't skip if some users exist - check each user individually
        log.info("Current user count: {}", userRepository.count());

        // Create admin user
        createUser(
                "admin",
                "admin@biometric.com",
                "password123",
                "Адміністратор",
                "Системи",
                Gender.MALE,
                LocalDate.of(1985, 5, 15),
                "+380501234567",
                Role.ADMIN
        );

        // Create doctor user
        createUser(
                "doctor",
                "doctor@biometric.com",
                "password123",
                "Доктор",
                "Іванов",
                Gender.MALE,
                LocalDate.of(1980, 3, 10),
                "+380671234567",
                Role.DOCTOR
        );

        // Create regular users
        createUser(
                "john_doe",
                "john@example.com",
                "password123",
                "John",
                "Doe",
                Gender.MALE,
                LocalDate.of(1990, 7, 20),
                "+380631234567",
                Role.USER
        );

        createUser(
                "maria_smith",
                "maria@example.com",
                "password123",
                "Maria",
                "Smith",
                Gender.FEMALE,
                LocalDate.of(1995, 2, 14),
                "+380951234567",
                Role.USER
        );

        createUser(
                "olena_koval",
                "olena@example.com",
                "password123",
                "Олена",
                "Коваль",
                Gender.FEMALE,
                LocalDate.of(1988, 11, 30),
                "+380661234567",
                Role.USER
        );

        log.info("✓ Data initialization completed! Created 5 test users.");
        log.info("All users have password: password123");
    }

    private void createUser(String username, String email, String plainPassword,
                            String firstName, String lastName, Gender gender,
                            LocalDate dateOfBirth, String phoneNumber, Role role) {

        // Skip if user already exists - DON'T try to update
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("User with email {} already exists, skipping", email);
            return;
        }

        if (userRepository.findByUsername(username).isPresent()) {
            log.info("User with username {} already exists, skipping", username);
            return;
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(plainPassword)) // ← Правильне кодування!
                .firstName(firstName)
                .lastName(lastName)
                .gender(gender)
                .dateOfBirth(dateOfBirth)
                .age(calculateAge(dateOfBirth))
                .phoneNumber(phoneNumber)
                .role(role)
                .active(true)
                .build(); // createdAt/updatedAt встановлюються автоматично Spring Data JPA

        userRepository.save(user);
        log.info("✓ Created user: {} ({})", username, email);
    }

    private Integer calculateAge(LocalDate dateOfBirth) {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}
