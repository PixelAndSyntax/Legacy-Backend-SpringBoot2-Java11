package com.example.legacydemo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;

/**
 * Service demonstrating multiple invalid @Async patterns that fail in Spring Boot 3.
 *
 * MIGRATION CHALLENGE: Spring Framework 6 enforces stricter @Async return type rules.
 * All invalid methods require manual refactoring to return void, Future, CompletableFuture,
 * or ListenableFuture.
 */
@Service
public class AsyncService {

  /**
   * INVALID PATTERN #1: Returning primitive String
   * Spring Boot 3 will reject this at startup
   */
  @Async
  @SuppressWarnings("unused")
  public String invalidAsyncReturn() {
    return "result";
  }

  /**
   * INVALID PATTERN #2: Returning custom domain object
   * Automation tools cannot determine how to wrap this in CompletableFuture
   * while preserving the business logic
   */
  @Async
  @SuppressWarnings("unused")
  public UserData invalidAsyncCustomObject() {
    return new UserData("user123", "email@example.com");
  }

  /**
   * INVALID PATTERN #3: Returning primitive int
   * Requires manual conversion to CompletableFuture<Integer>
   */
  @Async
  @SuppressWarnings("unused")
  public int invalidAsyncPrimitive() {
    return 42;
  }

  /**
   * INVALID PATTERN #4: Returning List
   * Complex refactoring needed when business logic builds the list asynchronously
   */
  @Async
  @SuppressWarnings("unused")
  public List<String> invalidAsyncList() {
    return List.of("item1", "item2", "item3");
  }

  /**
   * INVALID PATTERN #5: Returning Map with complex generic types
   * Automation tools struggle with proper generic type preservation
   */
  @Async
  @SuppressWarnings("unused")
  public Map<String, List<UserData>> invalidAsyncComplexGenerics() {
    return Map.of("users", List.of(new UserData("u1", "e1")));
  }

  /**
   * Valid signature across versions.
   */
  @Async
  public CompletableFuture<String> validAsyncReturn() {
    return CompletableFuture.completedFuture("ok");
  }

  /**
   * INVALID PATTERN #6: Void with exception handling that needs refactoring
   * While void is valid, the exception handling pattern may need updates
   */
  @Async
  @SuppressWarnings("unused")
  public void asyncWithComplexExceptionHandling() throws CustomAsyncException {
    try {
      // Complex business logic
      throw new RuntimeException("Simulated error");
    } catch (Exception e) {
      // This exception handling pattern may not work the same in Spring 6
      throw new CustomAsyncException("Wrapped: " + e.getMessage());
    }
  }

  // Helper classes for demonstration
  public static class UserData {
    private String userId;
    private String email;

    public UserData(String userId, String email) {
      this.userId = userId;
      this.email = email;
    }

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
  }

  public static class CustomAsyncException extends Exception {
    public CustomAsyncException(String message) {
      super(message);
    }
  }
}
