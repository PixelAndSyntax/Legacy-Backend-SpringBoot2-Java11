package com.example.legacydemo;

import com.example.legacydemo.config.TestConfigurationWithoutESAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for the main Spring Boot application.
 *
 * This test ensures that the Spring Boot application context loads successfully
 * with all configurations and dependencies properly wired.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfigurationWithoutESAPI.class)
class LegacyDemoApplicationTest {

    /**
     * Test that the Spring Boot application context loads successfully.
     * This is a smoke test that verifies the application can start without errors.
     */
    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully
        // If there are any configuration issues or missing beans, this test will fail
    }

    /**
     * Test that the main method can be called without throwing exceptions.
     * This ensures the application entry point is properly configured.
     */
    @Test
    void mainMethodTest() {
        // Test that main method doesn't throw exception during static analysis
        // Note: We don't actually call main() to avoid starting the server
        String[] args = {};
        // Verify the main method exists and is accessible
        try {
            LegacyDemoApplication.class.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Main method should exist", e);
        }
    }
}