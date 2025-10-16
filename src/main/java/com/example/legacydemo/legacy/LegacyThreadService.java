package com.example.legacydemo.legacy;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Demonstrates deprecated Thread APIs removed in Java 17.
 *
 * MIGRATION CHALLENGE: Thread.stop(), Thread.stop(Throwable), and Thread.destroy()
 * are completely removed. Requires redesign of thread lifecycle management with
 * cooperative cancellation patterns.
 *
 * DO NOT use in production. Replace with cooperative interruption.
 */
@Service
public class LegacyThreadService {

  /**
   * PATTERN #1: Thread.stop(Throwable) - removed in Java 17
   * Requires refactoring to use interrupt() + volatile flags
   */
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

  /**
   * PATTERN #2: Thread.destroy() - removed in Java 17
   * Demonstrates the pattern that never worked but was in API
   */
  public String demonstrateThreadDestroy() {
    Thread t = new Thread(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }, "destroy-demo-thread");
    t.start();

    // DEPRECATED: Never implemented, removed in Java 17
    // t.destroy(); // This would throw UnsupportedOperationException even in Java 11

    t.interrupt();
    return "Thread.destroy() was deprecated (never worked). Use interrupt() + flags.";
  }

  /**
   * PATTERN #3: Complex thread lifecycle with multiple deprecated patterns
   * Shows real-world complexity that automation cannot handle
   */
  public String complexThreadLifecyclePattern() {
    final AtomicBoolean shouldRun = new AtomicBoolean(true);

    Thread worker = new Thread(() -> {
      while (shouldRun.get()) {
        try {
          // Simulate work
          Thread.sleep(10);
        } catch (InterruptedException e) {
          // Old pattern: ignore interruption and use external flag
          // This pattern is common in legacy code
        }
      }
    }, "complex-worker");

    Thread monitor = new Thread(() -> {
      try {
        Thread.sleep(100);
        // Old pattern: forcefully stop the worker
        // worker.stop(); // Would be called here in legacy code
        shouldRun.set(false);
      } catch (InterruptedException ignored) {}
    }, "monitor-thread");

    worker.start();
    monitor.start();

    return "Complex thread pattern with mixed stop/flag usage. Requires manual refactoring.";
  }

  /**
   * PATTERN #4: Thread pool with unsafe termination
   * Common pattern in legacy applications that used stop()
   */
  public String unsafeThreadPoolTermination() {
    Thread[] workers = new Thread[5];

    for (int i = 0; i < workers.length; i++) {
      final int workerNum = i;
      workers[i] = new Thread(() -> {
        while (true) {
          try {
            Thread.sleep(100);
            // Simulated work
          } catch (InterruptedException e) {
            // Legacy pattern: ignore interruption
          }
        }
      }, "worker-" + workerNum);
      workers[i].start();
    }

    // Legacy shutdown pattern using stop()
    // for (Thread worker : workers) {
    //     worker.stop(); // Unsafe, removed in Java 17
    // }

    // Clean shutdown (commented for demo)
    for (Thread worker : workers) {
      worker.interrupt();
    }

    return "Thread pool with unsafe stop() pattern. Needs cooperative shutdown redesign.";
  }

  /**
   * PATTERN #5: Thread.suspend() and Thread.resume() - deprecated since Java 1.2
   * While deprecated earlier, still referenced in very old legacy code
   */
  @SuppressWarnings("deprecation")
  public String demonstrateSuspendResume() {
    Thread t = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          break;
        }
      }
    }, "suspend-demo");
    t.start();

    // These were deprecated in Java 1.2 but still exist in Java 11
    // They cause deadlock-prone code
    // t.suspend(); // Deadlock-prone
    // t.resume();  // Paired with suspend

    return "Thread.suspend()/resume() deprecated. Use wait()/notify() or locks.";
  }
}
