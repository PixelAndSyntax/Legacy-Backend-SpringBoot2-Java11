package com.example.legacydemo.controller;

import com.example.legacydemo.service.ESAPISecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST Controller demonstrating OWASP ESAPI security features
 *
 * This controller showcases various security capabilities:
 * - Input validation and sanitization
 * - XSS prevention through proper encoding
 * - SQL injection prevention
 * - Secure token generation
 * - File path validation
 * - Security auditing
 */
@RestController
@RequestMapping("/api/security")
@Profile("!test")
public class ESAPISecurityController {

    private final ESAPISecurityService securityService;

    public ESAPISecurityController(ESAPISecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Validates and sanitizes user input
     *
     * Example: GET /api/security/validate?input=<script>alert('xss')</script>&context=userComment
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(
            @RequestParam String input,
            @RequestParam(defaultValue = "general") String context) {

        Map<String, Object> result = securityService.validateAndSanitizeInput(input, context);
        return ResponseEntity.ok(result);
    }

    /**
     * Validates email addresses
     *
     * Example: POST /api/security/validate-email
     */
    @PostMapping("/validate-email")
    public ResponseEntity<Map<String, Object>> validateEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> result = securityService.validateEmail(email);
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates SQL injection prevention
     *
     * Example: GET /api/security/safe-query?table=users&column=username&value=admin'; DROP TABLE users; --
     */
    @GetMapping("/safe-query")
    public ResponseEntity<Map<String, Object>> prepareSafeQuery(
            @RequestParam String table,
            @RequestParam String column,
            @RequestParam String value) {

        Map<String, Object> result = securityService.prepareSafeQuery(table, column, value);
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates XSS prevention through encoding
     *
     * Example: POST /api/security/prevent-xss
     */
    @PostMapping("/prevent-xss")
    public ResponseEntity<Map<String, Object>> preventXSS(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        Map<String, Object> result = securityService.preventXSS(content);
        return ResponseEntity.ok(result);
    }

    /**
     * Generates secure random tokens
     *
     * Example: GET /api/security/generate-token
     */
    @GetMapping("/generate-token")
    public ResponseEntity<Map<String, Object>> generateSecureToken() {
        Map<String, Object> result = securityService.generateSecureToken();
        return ResponseEntity.ok(result);
    }

    /**
     * Validates file paths to prevent directory traversal
     *
     * Example: GET /api/security/validate-path?path=../../etc/passwd
     */
    @GetMapping("/validate-path")
    public ResponseEntity<Map<String, Object>> validateFilePath(@RequestParam String path) {
        Map<String, Object> result = securityService.validateFilePath(path);
        return ResponseEntity.ok(result);
    }

    /**
     * Security audit endpoint
     *
     * Example: GET /api/security/audit
     */
    @GetMapping("/audit")
    public ResponseEntity<Map<String, Object>> securityAudit() {
        Map<String, Object> result = securityService.getSecurityAudit();
        return ResponseEntity.ok(result);
    }

    /**
     * Comprehensive security test endpoint
     *
     * Example: POST /api/security/comprehensive-test
     */
    @PostMapping("/comprehensive-test")
    public ResponseEntity<Map<String, Object>> comprehensiveSecurityTest(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {

        Map<String, Object> testResults = new LinkedHashMap<>();

        // Test input validation
        String testInput = request.getOrDefault("input", "<script>alert('test')</script>");
        testResults.put("inputValidation", securityService.validateAndSanitizeInput(testInput, "test"));

        // Test XSS prevention
        String xssContent = request.getOrDefault("xssContent", "<img src=x onerror=alert('XSS')>");
        testResults.put("xssPrevention", securityService.preventXSS(xssContent));

        // Test SQL injection prevention
        String sqlValue = request.getOrDefault("sqlValue", "'; DROP TABLE users; --");
        testResults.put("sqlInjectionPrevention", securityService.prepareSafeQuery("users", "id", sqlValue));

        // Test email validation
        String email = request.getOrDefault("email", "test@<script>alert('xss')</script>.com");
        testResults.put("emailValidation", securityService.validateEmail(email));

        // Test file path validation
        String filePath = request.getOrDefault("filePath", "../../../etc/passwd");
        testResults.put("filePathValidation", securityService.validateFilePath(filePath));

        // Generate secure token
        testResults.put("tokenGeneration", securityService.generateSecureToken());

        // Add request info
        Map<String, Object> requestInfo = new LinkedHashMap<>();
        requestInfo.put("userAgent", httpRequest.getHeader("User-Agent"));
        requestInfo.put("remoteAddr", httpRequest.getRemoteAddr());
        requestInfo.put("requestURI", httpRequest.getRequestURI());
        testResults.put("requestInfo", requestInfo);

        return ResponseEntity.ok(testResults);
    }

    /**
     * Security headers demonstration
     *
     * Example: GET /api/security/headers
     */
    @GetMapping("/headers")
    public ResponseEntity<Map<String, Object>> securityHeaders(HttpServletRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Common security headers to check
        String[] securityHeaders = {
            "Content-Security-Policy",
            "X-Content-Type-Options",
            "X-Frame-Options",
            "X-XSS-Protection",
            "Strict-Transport-Security",
            "Referrer-Policy"
        };

        Map<String, String> headers = new LinkedHashMap<>();
        for (String header : securityHeaders) {
            headers.put(header, request.getHeader(header));
        }

        result.put("securityHeaders", headers);
        result.put("userAgent", request.getHeader("User-Agent"));
        result.put("recommendation", "Configure security headers in your web server or Spring Security");

        return ResponseEntity.ok()
                .header("X-Content-Type-Options", "nosniff")
                .header("X-Frame-Options", "DENY")
                .header("X-XSS-Protection", "1; mode=block")
                .body(result);
    }

    /**
     * Security best practices endpoint
     *
     * Example: GET /api/security/best-practices
     */
    @GetMapping("/best-practices")
    public ResponseEntity<Map<String, Object>> securityBestPractices() {
        Map<String, Object> practices = new LinkedHashMap<>();

        practices.put("inputValidation", java.util.Arrays.asList(
            "Always validate input on the server side",
            "Use whitelist validation instead of blacklist",
            "Validate data type, length, format, and range",
            "Reject invalid input rather than sanitizing when possible"
        ));

        practices.put("outputEncoding", java.util.Arrays.asList(
            "Encode output based on context (HTML, JavaScript, CSS, URL)",
            "Use proper encoding functions for each output context",
            "Never trust user input in any output context",
            "Use Content Security Policy (CSP) headers"
        ));

        practices.put("sqlInjectionPrevention", java.util.Arrays.asList(
            "Use parameterized queries (prepared statements)",
            "Validate and sanitize database inputs",
            "Use stored procedures with proper input validation",
            "Apply principle of least privilege to database accounts"
        ));

        practices.put("authenticationSecurity", java.util.Arrays.asList(
            "Use strong password policies",
            "Implement account lockout mechanisms",
            "Use secure session management",
            "Implement multi-factor authentication"
        ));

        practices.put("encryptionPractices", java.util.Arrays.asList(
            "Use HTTPS for all communications",
            "Encrypt sensitive data at rest",
            "Use strong encryption algorithms (AES-256)",
            "Properly manage encryption keys"
        ));

        return ResponseEntity.ok(practices);
    }
}