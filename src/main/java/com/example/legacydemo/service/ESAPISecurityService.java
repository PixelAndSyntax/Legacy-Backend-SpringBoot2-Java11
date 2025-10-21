package com.example.legacydemo.service;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.Validator;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * OWASP ESAPI Security Service for the Legacy Application
 *
 * Demonstrates enterprise security capabilities including:
 * - Input validation and sanitization
 * - Output encoding for XSS prevention
 * - SQL injection prevention
 * - Security logging and intrusion detection
 * - Secure random number generation
 * - Authentication helpers
 */
@Service
@Profile("!test")
public class ESAPISecurityService {

    private final Encoder encoder;
    private final Validator validator;

    // Common validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );

    public ESAPISecurityService() {
        // Initialize ESAPI components
        this.encoder = ESAPI.encoder();
        this.validator = ESAPI.validator();
    }

    /**
     * Validates and sanitizes user input to prevent injection attacks
     */
    public Map<String, Object> validateAndSanitizeInput(String input, String context) {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            // Basic input validation
            if (input == null || input.trim().isEmpty()) {
                result.put("status", "INVALID");
                result.put("error", "Input cannot be null or empty");
                return result;
            }

            // Length validation
            String validatedInput = validator.getValidInput(
                context,
                input,
                "SafeString",
                200,
                false
            );

            // Sanitize for different contexts
            try {
                String htmlEncoded = encoder.encodeForHTML(validatedInput);
                String jsEncoded = encoder.encodeForJavaScript(validatedInput);
                String urlEncoded = encoder.encodeForURL(validatedInput);
                String sqlEncoded = encoder.encodeForSQL(new org.owasp.esapi.codecs.MySQLCodec(org.owasp.esapi.codecs.MySQLCodec.Mode.STANDARD), validatedInput);

                result.put("status", "VALID");
                result.put("original", input);
                result.put("validated", validatedInput);
                result.put("htmlEncoded", htmlEncoded);
                result.put("jsEncoded", jsEncoded);
                result.put("urlEncoded", urlEncoded);
                result.put("sqlEncoded", sqlEncoded);

            } catch (EncodingException e) {
                result.put("status", "ENCODING_ERROR");
                result.put("error", "Encoding failed: " + e.getMessage());
            }

        } catch (ValidationException e) {
            result.put("status", "VALIDATION_ERROR");
            result.put("error", "Input validation failed: " + e.getMessage());
            result.put("logMessage", e.getLogMessage());

            // Log security event (using standard logging instead of ESAPI logger)
            System.err.println("ESAPI Security Warning: Input validation failed for context: " + context + " - " + e.getMessage());

        } catch (IntrusionException e) {
            result.put("status", "INTRUSION_DETECTED");
            result.put("error", "Potential attack detected: " + e.getMessage());

            // Log intrusion attempt (using standard logging instead of ESAPI logger)
            System.err.println("ESAPI Security FATAL: Intrusion attempt detected for context: " + context + " - " + e.getMessage());
        }

        return result;
    }

    /**
     * Validates email addresses using ESAPI validation
     */
    public Map<String, Object> validateEmail(String email) {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            // Use ESAPI email validation
            String validEmail = validator.getValidInput(
                "Email Validation",
                email,
                "Email",
                100,
                false
            );

            result.put("status", "VALID");
            result.put("email", validEmail);
            result.put("encoded", encoder.encodeForHTML(validEmail));

        } catch (ValidationException e) {
            result.put("status", "INVALID");
            result.put("error", "Invalid email format: " + e.getMessage());

            // Additional pattern check for demonstration
            if (!EMAIL_PATTERN.matcher(email != null ? email : "").matches()) {
                result.put("patternMatch", false);
            }
        }

        return result;
    }

    /**
     * Demonstrates secure SQL query parameter encoding
     */
    public Map<String, Object> prepareSafeQuery(String tableName, String columnName, String userInput) {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            // Validate table and column names (should be whitelisted in real applications)
            String safeTableName = validator.getValidInput(
                "Table Name",
                tableName,
                "SafeString",
                50,
                false
            );

            String safeColumnName = validator.getValidInput(
                "Column Name",
                columnName,
                "SafeString",
                50,
                false
            );

            // Encode user input for SQL
            String safeSqlValue = encoder.encodeForSQL(
                new org.owasp.esapi.codecs.MySQLCodec(org.owasp.esapi.codecs.MySQLCodec.Mode.STANDARD),
                userInput
            );

            // Build safe query (parameterized queries are still preferred)
            String safeQuery = String.format(
                "SELECT * FROM %s WHERE %s = '%s'",
                safeTableName,
                safeColumnName,
                safeSqlValue
            );

            result.put("status", "SUCCESS");
            result.put("safeQuery", safeQuery);
            result.put("originalInput", userInput);
            result.put("encodedInput", safeSqlValue);
            result.put("warning", "Use parameterized queries for production code");

        } catch (ValidationException e) {
            result.put("status", "ERROR");
            result.put("error", "Query preparation failed: " + e.getMessage());
        }

        return result;
    }

    /**
     * Demonstrates XSS prevention through proper encoding
     */
    public Map<String, Object> preventXSS(String userContent) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Various encoding strategies for different contexts
        String htmlEncoded = encoder.encodeForHTML(userContent);
        String htmlAttributeEncoded = encoder.encodeForHTMLAttribute(userContent);
        String jsEncoded = encoder.encodeForJavaScript(userContent);
        String cssEncoded = encoder.encodeForCSS(userContent);
        String xmlEncoded = encoder.encodeForXML(userContent);

        result.put("original", userContent);
        result.put("htmlEncoded", htmlEncoded);
        result.put("htmlAttributeEncoded", htmlAttributeEncoded);
        result.put("javascriptEncoded", jsEncoded);
        result.put("cssEncoded", cssEncoded);
        result.put("xmlEncoded", xmlEncoded);

        // Check for potential XSS patterns
        boolean suspiciousContent = userContent != null && (
            userContent.toLowerCase().contains("<script") ||
            userContent.toLowerCase().contains("javascript:") ||
            userContent.toLowerCase().contains("onload=") ||
            userContent.toLowerCase().contains("onerror=")
        );

        result.put("suspiciousContent", suspiciousContent);

        if (suspiciousContent) {
            System.err.println("ESAPI Security Warning: Potential XSS content detected: " + userContent);
        }

        return result;
    }

    /**
     * Generates secure random tokens for CSRF protection or session IDs
     */
    public Map<String, Object> generateSecureToken() {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            // Generate secure random string using available character sets
            String sessionToken = ESAPI.randomizer().getRandomString(32,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray());

            String csrfToken = ESAPI.randomizer().getRandomString(16,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray());

            // Generate secure random integers
            int secureInt = ESAPI.randomizer().getRandomInteger(1000, 9999);

            result.put("sessionToken", sessionToken);
            result.put("csrfToken", csrfToken);
            result.put("secureRandomInt", secureInt);
            result.put("tokenLength", sessionToken.length());

        } catch (Exception e) {
            result.put("error", "Token generation failed: " + e.getMessage());
        }

        return result;
    }

    /**
     * Demonstrates secure file path validation
     */
    public Map<String, Object> validateFilePath(String filePath) {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            // Validate file path to prevent directory traversal
            String safeFilePath = validator.getValidInput(
                "File Path",
                filePath,
                "FileName",
                255,
                false
            );

            // Additional checks for directory traversal
            boolean containsTraversal = filePath != null && (
                filePath.contains("../") ||
                filePath.contains("..\\") ||
                filePath.contains("/..") ||
                filePath.contains("\\..")
            );

            result.put("status", "VALID");
            result.put("originalPath", filePath);
            result.put("safePath", safeFilePath);
            result.put("containsTraversal", containsTraversal);

            if (containsTraversal) {
                result.put("warning", "Directory traversal attempt detected");
                System.err.println("ESAPI Security Warning: Directory traversal attempt: " + filePath);
            }

        } catch (ValidationException e) {
            result.put("status", "INVALID");
            result.put("error", "File path validation failed: " + e.getMessage());
        }

        return result;
    }

    /**
     * Security audit information
     */
    public Map<String, Object> getSecurityAudit() {
        Map<String, Object> audit = new LinkedHashMap<>();

        audit.put("esapiVersion", "2.6.0.0");
        audit.put("encoderClass", encoder.getClass().getSimpleName());
        audit.put("validatorClass", validator.getClass().getSimpleName());
        audit.put("securityEnabled", true);
        audit.put("availableCodecs", java.util.Arrays.asList(
            "HTMLEntityCodec",
            "JavaScriptCodec",
            "MySQLCodec",
            "OracleCodec",
            "PercentCodec",
            "UnixCodec",
            "VBScriptCodec",
            "WindowsCodec",
            "XMLEntityCodec"
        ));

        return audit;
    }
}