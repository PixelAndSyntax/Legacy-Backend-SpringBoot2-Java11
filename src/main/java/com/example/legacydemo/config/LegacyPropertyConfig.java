package com.example.legacydemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Custom property source configuration using late-bound properties.
 *
 * MIGRATION CHALLENGE: @PropertySource with late-bound properties are often missed
 * by automated migration tools like spring-boot-properties-migrator because they
 * cannot be resolved at static analysis time.
 *
 * The properties file contains deprecated Spring Boot 2 property names that need
 * manual migration to Spring Boot 3 equivalents.
 */
@Configuration
@PropertySource("classpath:legacy-config.properties")
public class LegacyPropertyConfig {

    /**
     * This configuration demonstrates the challenge where:
     * 1. Properties are loaded from external files via @PropertySource
     * 2. Property names use deprecated Spring Boot 2 patterns
     * 3. Static analysis tools cannot detect these at compile time
     * 4. Runtime failures only occur when properties are actually accessed
     *
     * Manual intervention required to:
     * - Identify all @PropertySource files
     * - Review each property file for deprecated names
     * - Update property names to Spring Boot 3 conventions
     * - Test runtime behavior after migration
     */
}
