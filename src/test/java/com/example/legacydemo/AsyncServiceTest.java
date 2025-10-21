package com.example.legacydemo;

import com.example.legacydemo.service.AsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for AsyncService
 * Tests the various async method patterns that work in Spring Boot 2
 * but will fail in Spring Boot 3 due to stricter @Async return type rules
 */
@SpringBootTest
@ActiveProfiles("test")
class AsyncServiceTest {

  @Autowired
  private AsyncService asyncService;

  @BeforeEach
  void setUp() {
    assertNotNull(asyncService, "AsyncService should be autowired");
  }

  @Test
  void validAsyncReturnWorks() throws Exception {
    // Test the valid async method that works in both versions
    CompletableFuture<String> result = asyncService.validAsyncReturn();
    assertNotNull(result, "CompletableFuture should not be null");
    assertThat(result.get()).isEqualTo("ok");
  }













  @Test
  void testValidAsyncReturnMultipleCalls() throws Exception {
    // Test multiple calls to the valid async method
    CompletableFuture<String> result1 = asyncService.validAsyncReturn();
    CompletableFuture<String> result2 = asyncService.validAsyncReturn();

    assertThat(result1.get()).isEqualTo("ok");
    assertThat(result2.get()).isEqualTo("ok");

    assertTrue(result1.isDone(), "First call should be completed");
    assertTrue(result2.isDone(), "Second call should be completed");
  }

  @Test
  void testAsyncServiceConfiguration() {
    // Test that the async service is properly configured
    assertNotNull(asyncService, "AsyncService should be properly injected");

    // Verify async methods are available
    assertDoesNotThrow(() -> {
      asyncService.validAsyncReturn();
      asyncService.invalidAsyncReturn();
    }, "Async methods should be callable without exceptions");
  }
}