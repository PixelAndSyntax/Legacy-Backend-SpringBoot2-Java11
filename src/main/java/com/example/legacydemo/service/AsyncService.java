package com.example.legacydemo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

  /**
   * Invalid in Spring Framework 6 / Boot 3: @Async should return void/Future/CompletableFuture/ListenableFuture.
   * We keep this here to demonstrate the migration.
   */
  @Async
  @SuppressWarnings("unused")
  public String invalidAsyncReturn() {
    return "result";
  }

  /**
   * Valid signature across versions.
   */
  @Async
  public CompletableFuture<String> validAsyncReturn() {
    return CompletableFuture.completedFuture("ok");
  }
}
