package com.example.legacydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Boot 2.x + Java 11 baseline. This app intentionally uses APIs that are
 * deprecated/removed by Java 17 and Spring Boot 3 to simulate real-world upgrades.
 *
 * When you upgrade:
 *  - Move javax.* to jakarta.* (servlet, persistence, JAXB)
 *  - Replace deprecated Java APIs (Thread.stop/destroy, java.security.acl, Applet)
 *  - Replace javax.security.cert with java.security.cert
 *  - Consider replacing sun.misc.Unsafe with VarHandle
 */
@SpringBootApplication
@EnableAsync
public class LegacyDemoApplication {
  public static void main(String[] args) {
    SpringApplication.run(LegacyDemoApplication.class, args);
  }
}
