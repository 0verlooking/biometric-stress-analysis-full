package com.biometric.stressanalysis.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utility to generate BCrypt password hashes
 * Run this class to generate password hashes for data.sql
 */
@SpringBootApplication
public class PasswordHashGenerator {

    public static void main(String[] args) {
        SpringApplication.run(PasswordHashGenerator.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner generateHashes(PasswordEncoder passwordEncoder) {
        return args -> {
            String password = "password123";
            String hash = passwordEncoder.encode(password);

            System.out.println("=".repeat(80));
            System.out.println("BCrypt Hash Generator");
            System.out.println("=".repeat(80));
            System.out.println("Password: " + password);
            System.out.println("BCrypt Hash: " + hash);
            System.out.println("=".repeat(80));
            System.out.println("\nUse this hash in data.sql for all test users!");
            System.out.println("Copy the hash below:");
            System.out.println(hash);
            System.out.println("=".repeat(80));

            // Verify it works
            boolean matches = passwordEncoder.matches(password, hash);
            System.out.println("\nVerification: " + (matches ? "✓ CORRECT" : "✗ FAILED"));
        };
    }
}
