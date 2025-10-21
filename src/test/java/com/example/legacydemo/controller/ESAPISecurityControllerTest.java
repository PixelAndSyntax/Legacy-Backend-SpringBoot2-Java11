package com.example.legacydemo.controller;

import com.example.legacydemo.service.ESAPISecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for ESAPISecurityController
 *
 * Tests cover:
 * - All REST endpoints
 * - Request/Response handling
 * - Error scenarios
 * - Security features
 */
@WebMvcTest(ESAPISecurityController.class)
@ExtendWith(MockitoExtension.class)
class ESAPISecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ESAPISecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> mockValidationResult;
    private Map<String, Object> mockEmailResult;
    private Map<String, Object> mockQueryResult;
    private Map<String, Object> mockXssResult;
    private Map<String, Object> mockTokenResult;
    private Map<String, Object> mockPathResult;
    private Map<String, Object> mockAuditResult;

    @BeforeEach
    void setUp() {
        setupMockResults();
    }

    private void setupMockResults() {
        // Mock validation result
        mockValidationResult = new HashMap<>();
        mockValidationResult.put("status", "VALID");
        mockValidationResult.put("original", "test input");
        mockValidationResult.put("htmlEncoded", "&lt;test&gt;");

        // Mock email validation result
        mockEmailResult = new HashMap<>();
        mockEmailResult.put("status", "VALID");
        mockEmailResult.put("email", "test@example.com");
        mockEmailResult.put("encoded", "test@example.com");

        // Mock query result
        mockQueryResult = new HashMap<>();
        mockQueryResult.put("status", "SUCCESS");
        mockQueryResult.put("safeQuery", "SELECT * FROM users WHERE username = 'testuser'");
        mockQueryResult.put("originalInput", "testuser");

        // Mock XSS prevention result
        mockXssResult = new HashMap<>();
        mockXssResult.put("original", "<script>alert('xss')</script>");
        mockXssResult.put("htmlEncoded", "&lt;script&gt;alert('xss')&lt;/script&gt;");
        mockXssResult.put("suspiciousContent", true);

        // Mock token generation result
        mockTokenResult = new HashMap<>();
        mockTokenResult.put("sessionToken", "abc123def456");
        mockTokenResult.put("csrfToken", "xyz789");
        mockTokenResult.put("secureRandomInt", 1234);

        // Mock path validation result
        mockPathResult = new HashMap<>();
        mockPathResult.put("status", "VALID");
        mockPathResult.put("originalPath", "documents/file.txt");
        mockPathResult.put("containsTraversal", false);

        // Mock audit result
        mockAuditResult = new HashMap<>();
        mockAuditResult.put("esapiVersion", "2.6.0.0");
        mockAuditResult.put("securityEnabled", true);
    }

    @Test
    void testValidateInput_Success() throws Exception {
        // Given
        when(securityService.validateAndSanitizeInput(anyString(), anyString()))
            .thenReturn(mockValidationResult);

        // When & Then
        mockMvc.perform(get("/api/security/validate")
                .param("input", "test input")
                .param("context", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("VALID"))
                .andExpect(jsonPath("$.original").value("test input"))
                .andExpect(jsonPath("$.htmlEncoded").exists());
    }

    @Test
    void testValidateInput_WithDefaultContext() throws Exception {
        // Given
        when(securityService.validateAndSanitizeInput(anyString(), anyString()))
            .thenReturn(mockValidationResult);

        // When & Then
        mockMvc.perform(get("/api/security/validate")
                .param("input", "test input"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("VALID"));
    }

    @Test
    void testValidateEmail_Success() throws Exception {
        // Given
        when(securityService.validateEmail(anyString()))
            .thenReturn(mockEmailResult);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "test@example.com");

        // When & Then
        mockMvc.perform(post("/api/security/validate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("VALID"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testValidateEmail_EmptyBody() throws Exception {
        // Given
        when(securityService.validateEmail(null))
            .thenReturn(mockEmailResult);

        Map<String, String> requestBody = new HashMap<>();

        // When & Then
        mockMvc.perform(post("/api/security/validate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());
    }

    @Test
    void testPrepareSafeQuery_Success() throws Exception {
        // Given
        when(securityService.prepareSafeQuery(anyString(), anyString(), anyString()))
            .thenReturn(mockQueryResult);

        // When & Then
        mockMvc.perform(get("/api/security/safe-query")
                .param("table", "users")
                .param("column", "username")
                .param("value", "testuser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.safeQuery").exists())
                .andExpect(jsonPath("$.originalInput").value("testuser"));
    }

    @Test
    void testPreventXSS_Success() throws Exception {
        // Given
        when(securityService.preventXSS(anyString()))
            .thenReturn(mockXssResult);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("content", "<script>alert('xss')</script>");

        // When & Then
        mockMvc.perform(post("/api/security/prevent-xss")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.original").value("<script>alert('xss')</script>"))
                .andExpect(jsonPath("$.htmlEncoded").exists())
                .andExpect(jsonPath("$.suspiciousContent").value(true));
    }

    @Test
    void testGenerateSecureToken_Success() throws Exception {
        // Given
        when(securityService.generateSecureToken())
            .thenReturn(mockTokenResult);

        // When & Then
        mockMvc.perform(get("/api/security/generate-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sessionToken").value("abc123def456"))
                .andExpect(jsonPath("$.csrfToken").value("xyz789"))
                .andExpect(jsonPath("$.secureRandomInt").value(1234));
    }

    @Test
    void testValidateFilePath_Success() throws Exception {
        // Given
        when(securityService.validateFilePath(anyString()))
            .thenReturn(mockPathResult);

        // When & Then
        mockMvc.perform(get("/api/security/validate-path")
                .param("path", "documents/file.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("VALID"))
                .andExpect(jsonPath("$.originalPath").value("documents/file.txt"))
                .andExpect(jsonPath("$.containsTraversal").value(false));
    }

    @Test
    void testSecurityAudit_Success() throws Exception {
        // Given
        when(securityService.getSecurityAudit())
            .thenReturn(mockAuditResult);

        // When & Then
        mockMvc.perform(get("/api/security/audit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.esapiVersion").value("2.6.0.0"))
                .andExpect(jsonPath("$.securityEnabled").value(true));
    }

    @Test
    void testComprehensiveSecurityTest_Success() throws Exception {
        // Given
        when(securityService.validateAndSanitizeInput(anyString(), anyString()))
            .thenReturn(mockValidationResult);
        when(securityService.preventXSS(anyString()))
            .thenReturn(mockXssResult);
        when(securityService.prepareSafeQuery(anyString(), anyString(), anyString()))
            .thenReturn(mockQueryResult);
        when(securityService.validateEmail(anyString()))
            .thenReturn(mockEmailResult);
        when(securityService.validateFilePath(anyString()))
            .thenReturn(mockPathResult);
        when(securityService.generateSecureToken())
            .thenReturn(mockTokenResult);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("input", "test input");
        requestBody.put("xssContent", "<script>alert('xss')</script>");
        requestBody.put("sqlValue", "'; DROP TABLE users; --");
        requestBody.put("email", "test@example.com");
        requestBody.put("filePath", "../../../etc/passwd");

        // When & Then
        mockMvc.perform(post("/api/security/comprehensive-test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.inputValidation").exists())
                .andExpect(jsonPath("$.xssPrevention").exists())
                .andExpect(jsonPath("$.sqlInjectionPrevention").exists())
                .andExpect(jsonPath("$.emailValidation").exists())
                .andExpect(jsonPath("$.filePathValidation").exists())
                .andExpect(jsonPath("$.tokenGeneration").exists())
                .andExpect(jsonPath("$.requestInfo").exists());
    }

    @Test
    void testComprehensiveSecurityTest_WithDefaults() throws Exception {
        // Given
        when(securityService.validateAndSanitizeInput(anyString(), anyString()))
            .thenReturn(mockValidationResult);
        when(securityService.preventXSS(anyString()))
            .thenReturn(mockXssResult);
        when(securityService.prepareSafeQuery(anyString(), anyString(), anyString()))
            .thenReturn(mockQueryResult);
        when(securityService.validateEmail(anyString()))
            .thenReturn(mockEmailResult);
        when(securityService.validateFilePath(anyString()))
            .thenReturn(mockPathResult);
        when(securityService.generateSecureToken())
            .thenReturn(mockTokenResult);

        Map<String, String> requestBody = new HashMap<>();

        // When & Then
        mockMvc.perform(post("/api/security/comprehensive-test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.inputValidation").exists())
                .andExpect(jsonPath("$.xssPrevention").exists())
                .andExpect(jsonPath("$.sqlInjectionPrevention").exists())
                .andExpect(jsonPath("$.emailValidation").exists())
                .andExpect(jsonPath("$.filePathValidation").exists())
                .andExpect(jsonPath("$.tokenGeneration").exists());
    }

    @Test
    void testSecurityHeaders_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/security/headers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"))
                .andExpect(jsonPath("$.securityHeaders").exists())
                .andExpect(jsonPath("$.recommendation").exists());
    }

    @Test
    void testSecurityBestPractices_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/security/best-practices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.inputValidation").exists())
                .andExpect(jsonPath("$.outputEncoding").exists())
                .andExpect(jsonPath("$.sqlInjectionPrevention").exists())
                .andExpect(jsonPath("$.authenticationSecurity").exists())
                .andExpect(jsonPath("$.encryptionPractices").exists());
    }

    @Test
    void testValidateInput_MissingParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/security/validate"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidateEmail_InvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/security/validate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPreventXSS_InvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/security/prevent-xss")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidateFilePath_MissingParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/security/validate-path"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPrepareSafeQuery_MissingParameters() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/security/safe-query")
                .param("table", "users"))
                .andExpect(status().isBadRequest());
    }
}