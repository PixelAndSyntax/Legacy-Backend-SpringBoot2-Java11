package com.example.legacydemo.legacy;

import org.springframework.stereotype.Service;

/**
 * Demonstrates deprecated Thread.stop(Throwable) removed in Java 17.
 * Do NOT use in production. Replace with cooperative interruption.
 */
@Service
public class LegacyThreadService {
  public String demonstrateDeprecatedStop() {
    Thread t = new Thread(() -> {
      try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    }, "legacy-demo-thread");
    t.start();
    // DEPRECATED: Removed in Java 17
    try {
      // t.stop(new RuntimeException("Stopping thread (DEMO ONLY)")); // commented to avoid abrupt termination
      return "Thread created; deprecated stop() referenced (commented). Replace with interrupt + flags.";
    } finally {
      t.interrupt();
    }
  }
}
