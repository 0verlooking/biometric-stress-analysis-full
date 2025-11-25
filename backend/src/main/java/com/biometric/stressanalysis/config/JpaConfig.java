package com.biometric.stressanalysis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration
 * Enables automatic auditing for @CreatedDate and @LastModifiedDate
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
