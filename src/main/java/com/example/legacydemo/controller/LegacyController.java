package com.example.legacydemo.controller;

import com.example.legacydemo.domain.Customer;
import com.example.legacydemo.domain.XmlCustomer;
import com.example.legacydemo.repo.CustomerRepository;
import com.example.legacydemo.service.AsyncService;
import com.example.legacydemo.legacy.AppletRef;
import com.example.legacydemo.legacy.LegacyThreadService;
import com.example.legacydemo.legacy.UnsafeDemo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest; // javax.* on purpose (Jakarta in Boot 3)
import java.util.*;

@RestController
@RequestMapping("/api")
public class LegacyController {

  private final CustomerRepository repo;
  private final AsyncService asyncService;
  private final LegacyThreadService legacyThreadService;

  public LegacyController(CustomerRepository repo, AsyncService asyncService, LegacyThreadService legacyThreadService) {
    this.repo = repo;
    this.asyncService = asyncService;
    this.legacyThreadService = legacyThreadService;
  }

  /**
   * Demonstrates javax.servlet usage which will become jakarta.servlet in Boot 3.
   */
  @GetMapping("/hello")
  public Map<String, Object> hello(HttpServletRequest request) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("message", "Hello from Boot 2.x on Java 11");
    map.put("userAgent", request.getHeader("User-Agent"));
    return map;
  }

  /**
   * JAXB using javax.xml.bind annotations. On Boot 3/Java 17 use jakarta.xml.bind.
   */
  @GetMapping(value = "/xml/customer", produces = MediaType.APPLICATION_XML_VALUE)
  public XmlCustomer xmlCustomer() {
    return new XmlCustomer(1L, "Alice", "alice@example.com");
  }

  /**
   * JPA with javax.persistence annotations (changes to jakarta.persistence in Boot 3).
   */
  @GetMapping("/customers")
  public List<Customer> customers() {
    return repo.findAll();
  }

  /**
   * Intentionally uses deprecated Thread APIs to showcase migration.
   */
  @GetMapping("/thread/stop-demo")
  public String threadStopDemo() {
    return legacyThreadService.demonstrateDeprecatedStop();
  }

  /**
   * Uses sun.misc.Unsafe reflectively (not recommended) to show migration to VarHandle.
   */
  @GetMapping("/unsafe")
  public Map<String, Object> unsafeInfo() {
    Map<String, Object> info = new LinkedHashMap<>();
    info.put("unsafeAvailable", UnsafeDemo.isAvailable());
    info.put("addressSize", UnsafeDemo.addressSize());
    return info;
  }

  /**
   * Shows reference to the Applet API (removed in Java 17).
   */
  @GetMapping("/applet")
  public Map<String, String> applet() {
    return Collections.singletonMap("appletClass", AppletRef.appletClassName());
  }
}
