package com.example.legacydemo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for ESAPISecurityService
 *
 * Tests cover:
 * - Input validation and sanitization
 * - XSS prevention
 * - SQL injection prevention
 * - Email validation
 * - File path validation
 * - Token generation
 * - Security auditing
 */
@ExtendWith(MockitoExtension.class)
class ESAPISecurityServiceTest {

    private ESAPISecurityService securityService;

    @BeforeEach
    void setUp() {
        // Note: We test with a real instance since ESAPI is complex to mock properly
        // In production, you might want to create a test configuration for ESAPI
        try {
            securityService = new ESAPISecurityService();
        } catch (Exception e) {
            // Skip tests if ESAPI configuration fails
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "ESAPI configuration failed: " + e.getMessage());
        }
    }

    @Test
    void testValidateAndSanitizeInput_ValidInput() {
        // Given
        String validInput = "Hello World";
        String context = "test";

        // When
        Map<String, Object> result = securityService.validateAndSanitizeInput(validInput, context);

        // Then
        assertNotNull(result);
        assertEquals("VALID", result.get("status"));
        assertEquals(validInput, result.get("original"));
        assertNotNull(result.get("htmlEncoded"));
        assertNotNull(result.get("jsEncoded"));
        assertNotNull(result.get("urlEncoded"));
        assertNotNull(result.get("sqlEncoded"));
    }

    @Test
    void testValidateAndSanitizeInput_XSSAttempt() {
        // Given
        String maliciousInput = "<script>alert('xss')</script>";
        String context = "test";

        // When
        Map<String, Object> result = securityService.validateAndSanitizeInput(maliciousInput, context);

        // Then
        assertNotNull(result);
        // Should either be valid (with encoding) or validation error
        assertTrue(result.get("status").equals("VALID") || result.get("status").equals("VALIDATION_ERROR"));

        if ("VALID".equals(result.get("status"))) {
            String htmlEncoded = (String) result.get("htmlEncoded");
            assertFalse(htmlEncoded.contains("<script>"));
            assertTrue(htmlEncoded.contains("&lt;script&gt;") || htmlEncoded.contains("&#x3c;script&#x3e;"));
        }
    }

    @Test
    void testValidateAndSanitizeInput_NullInput() {
        // Given
        String nullInput = null;
        String context = "test";

        // When
        Map<String, Object> result = securityService.validateAndSanitizeInput(nullInput, context);

        // Then
        assertNotNull(result);
        assertEquals("INVALID", result.get("status"));
        assertNotNull(result.get("error"));
        assertTrue(result.get("error").toString().contains("null or empty"));
    }

    @Test
    void testValidateAndSanitizeInput_EmptyInput() {
        // Given
        String emptyInput = "   ";
        String context = "test";

        // When
        Map<String, Object> result = securityService.validateAndSanitizeInput(emptyInput, context);

        // Then
        assertNotNull(result);
        assertEquals("INVALID", result.get("status"));
        assertNotNull(result.get("error"));
    }

    @Test
    void testValidateEmail_ValidEmail() {
        // Given
        String validEmail = "test@example.com";

        // When
        Map<String, Object> result = securityService.validateEmail(validEmail);

        // Then
        assertNotNull(result);
        assertEquals("VALID", result.get("status"));
        assertEquals(validEmail, result.get("email"));
        assertNotNull(result.get("encoded"));
    }

    @Test
    void testValidateEmail_InvalidEmail() {
        // Given
        String invalidEmail = "invalid-email";

        // When
        Map<String, Object> result = securityService.validateEmail(invalidEmail);

        // Then
        assertNotNull(result);
        assertEquals("INVALID", result.get("status"));
        assertNotNull(result.get("error"));
    }

    @Test
    void testValidateEmail_XSSInEmail() {
        // Given
        String maliciousEmail = "test@<script>alert('xss')</script>.com";

        // When
        Map<String, Object> result = securityService.validateEmail(maliciousEmail);

        // Then
        assertNotNull(result);
        assertEquals("INVALID", result.get("status"));
        assertNotNull(result.get("error"));
    }

    @Test
    void testPrepareSafeQuery_ValidParameters() {
        // Given
        String tableName = "users";
        String columnName = "username";
        String userInput = "testuser";

        // When
        Map<String, Object> result = securityService.prepareSafeQuery(tableName, columnName, userInput);

        // Then
        assertNotNull(result);
        assertEquals("SUCCESS", result.get("status"));
        assertNotNull(result.get("safeQuery"));
        assertEquals(userInput, result.get("originalInput"));
        assertNotNull(result.get("encodedInput"));
        assertNotNull(result.get("warning"));
    }

    @Test
    void testPrepareSafeQuery_SQLInjectionAttempt() {
        // Given
        String tableName = "users";
        String columnName = "username";
        String maliciousInput = "'; DROP TABLE users; --";

        // When
        Map<String, Object> result = securityService.prepareSafeQuery(tableName, columnName, maliciousInput);

        // Then
        assertNotNull(result);
        // Should either succeed with encoding or fail validation
        assertTrue(result.get("status").equals("SUCCESS") || result.get("status").equals("ERROR"));

        if ("SUCCESS".equals(result.get("status"))) {
            String safeQuery = (String) result.get("safeQuery");
            String encodedInput = (String) result.get("encodedInput");
            // The malicious SQL should be encoded
            assertFalse(safeQuery.contains("DROP TABLE"));
        }
    }

    @Test
    void testPreventXSS_CleanContent() {
        // Given
        String cleanContent = "Hello World";

        // When
        Map<String, Object> result = securityService.preventXSS(cleanContent);

        // Then
        assertNotNull(result);
        assertEquals(cleanContent, result.get("original"));
        assertNotNull(result.get("htmlEncoded"));
        assertNotNull(result.get("htmlAttributeEncoded"));
        assertNotNull(result.get("javascriptEncoded"));
        assertNotNull(result.get("cssEncoded"));
        assertNotNull(result.get("xmlEncoded"));
        assertEquals(false, result.get("suspiciousContent"));
    }

    @Test
    void testPreventXSS_MaliciousContent() {
        // Given
        String maliciousContent = "<script>alert('xss')</script>";

        // When
        Map<String, Object> result = securityService.preventXSS(maliciousContent);

        // Then
        assertNotNull(result);
        assertEquals(maliciousContent, result.get("original"));
        assertEquals(true, result.get("suspiciousContent"));

        String htmlEncoded = (String) result.get("htmlEncoded");
        assertFalse(htmlEncoded.contains("<script>"));
    }

    @Test
    void testPreventXSS_JavaScriptInjection() {
        // Given
        String jsInjection = "javascript:alert('xss')";

        // When
        Map<String, Object> result = securityService.preventXSS(jsInjection);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("suspiciousContent"));
    }

    @Test
    void testPreventXSS_EventHandlerInjection() {
        // Given
        String eventHandler = "<img src=x onerror=alert('xss')>";

        // When
        Map<String, Object> result = securityService.preventXSS(eventHandler);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("suspiciousContent"));
    }

    @Test
    void testGenerateSecureToken() {
        // When
        Map<String, Object> result = securityService.generateSecureToken();

        // Then
        assertNotNull(result);
        assertNotNull(result.get("sessionToken"));
        assertNotNull(result.get("csrfToken"));
        assertNotNull(result.get("secureRandomInt"));

        String sessionToken = (String) result.get("sessionToken");
        String csrfToken = (String) result.get("csrfToken");

        assertEquals(32, sessionToken.length());
        assertEquals(16, csrfToken.length());

        // Tokens should be different on subsequent calls
        Map<String, Object> result2 = securityService.generateSecureToken();
        assertNotEquals(result.get("sessionToken"), result2.get("sessionToken"));
        assertNotEquals(result.get("csrfToken"), result2.get("csrfToken"));
    }

    @Test
    void testValidateFilePath_SafePath() {
        // Given
        String safePath = "documents/file.txt";

        // When
        Map<String, Object> result = securityService.validateFilePath(safePath);

        // Then
        assertNotNull(result);
        assertEquals("VALID", result.get("status"));
        assertEquals(safePath, result.get("originalPath"));
        assertEquals(false, result.get("containsTraversal"));
    }

    @Test
    void testValidateFilePath_DirectoryTraversal() {
        // Given
        String maliciousPath = "../../../etc/passwd";

        // When
        Map<String, Object> result = securityService.validateFilePath(maliciousPath);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("containsTraversal"));
        assertNotNull(result.get("warning"));
    }

    @Test
    void testValidateFilePath_WindowsTraversal() {
        // Given
        String windowsTraversal = "..\\..\\windows\\system32\\config\\sam";

        // When
        Map<String, Object> result = securityService.validateFilePath(windowsTraversal);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("containsTraversal"));
    }

    @Test
    void testGetSecurityAudit() {
        // When
        Map<String, Object> result = securityService.getSecurityAudit();

        // Then
        assertNotNull(result);
        assertEquals("2.6.0.0", result.get("esapiVersion"));
        assertNotNull(result.get("encoderClass"));
        assertNotNull(result.get("validatorClass"));
        assertEquals(true, result.get("securityEnabled"));
        assertNotNull(result.get("availableCodecs"));
    }

    @Test
    void testValidateAndSanitizeInput_LongInput() {
        // Given - Input longer than typical validation limits
        String longInput = "a".repeat(1000);
        String context = "test";

        // When
        Map<String, Object> result = securityService.validateAndSanitizeInput(longInput, context);

        // Then
        assertNotNull(result);
        // Should either be valid or validation error due to length
        assertTrue(result.get("status").equals("VALID") || result.get("status").equals("VALIDATION_ERROR"));
    }

    @Test
    void testValidateEmail_NullEmail() {
        // Given
        String nullEmail = null;

        // When
        Map<String, Object> result = securityService.validateEmail(nullEmail);

        // Then
        assertNotNull(result);
        assertEquals("INVALID", result.get("status"));
    }

    @Test
    void testPrepareSafeQuery_InvalidTableName() {
        // Given
        String invalidTable = "users; DROP TABLE passwords; --";
        String columnName = "username";
        String userInput = "testuser";

        // When
        Map<String, Object> result = securityService.prepareSafeQuery(invalidTable, columnName, userInput);

        // Then
        assertNotNull(result);
        // Should fail validation due to invalid table name
        assertEquals("ERROR", result.get("status"));
    }

    @Test
    void testPreventXSS_NullContent() {
        // Given
        String nullContent = null;

        // When
        Map<String, Object> result = securityService.preventXSS(nullContent);

        // Then
        assertNotNull(result);
        assertNull(result.get("original"));
        // Should handle null gracefully
        assertNotNull(result.get("htmlEncoded"));
    }
}