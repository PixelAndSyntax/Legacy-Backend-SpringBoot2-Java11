package com.example.legacydemo.controller;

import com.example.legacydemo.domain.Customer;
import com.example.legacydemo.domain.XmlCustomer;
import com.example.legacydemo.repo.CustomerRepository;
import com.example.legacydemo.service.AsyncService;
import com.example.legacydemo.legacy.AppletRef;
import com.example.legacydemo.legacy.LegacyThreadService;
import com.example.legacydemo.legacy.UnsafeDemo;
import com.example.legacydemo.security.CustomAclManager;
import com.example.legacydemo.servlet.LegacySecurityFilter;
import com.example.legacydemo.util.ReflectiveApiCaller;
import com.example.legacydemo.xml.ComplexJaxbProcessor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest; // javax.* on purpose (Jakarta in Boot 3)
import javax.xml.bind.JAXBException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class LegacyController {

  private final CustomerRepository repo;
  private final AsyncService asyncService;
  private final LegacyThreadService legacyThreadService;
  private final CustomAclManager aclManager;
  private final LegacySecurityFilter securityFilter;
  private final ReflectiveApiCaller reflectiveApiCaller;
  private final ComplexJaxbProcessor jaxbProcessor;

  public LegacyController(CustomerRepository repo, AsyncService asyncService,
                         LegacyThreadService legacyThreadService,
                         CustomAclManager aclManager,
                         LegacySecurityFilter securityFilter,
                         ReflectiveApiCaller reflectiveApiCaller,
                         ComplexJaxbProcessor jaxbProcessor) {
    this.repo = repo;
    this.asyncService = asyncService;
    this.legacyThreadService = legacyThreadService;
    this.aclManager = aclManager;
    this.securityFilter = securityFilter;
    this.reflectiveApiCaller = reflectiveApiCaller;
    this.jaxbProcessor = jaxbProcessor;
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

  /**
   * Complex ACL security demonstration using java.security.acl (removed in Java 17)
   * MIGRATION CHALLENGE: Requires complete architectural redesign
   */
  @GetMapping("/security/acl")
  public Map<String, Object> aclDemo() {
    Map<String, Object> result = new LinkedHashMap<>();

    // Create principal
    Principal owner = () -> "admin";
    Principal user = () -> "john.doe";

    // Create ACL for resource
    aclManager.createAcl("document-123", owner);
    aclManager.grantPermission("document-123", owner, user, "READ");

    // Check permissions
    boolean canRead = aclManager.checkAccess("document-123", user, "READ");
    boolean canWrite = aclManager.checkAccess("document-123", user, "WRITE");

    result.put("resource", "document-123");
    result.put("user", user.getName());
    result.put("canRead", canRead);
    result.put("canWrite", canWrite);
    result.put("aclSummary", aclManager.getAclSummary());

    return result;
  }

  /**
   * Servlet filter information using javax.servlet APIs
   * MIGRATION CHALLENGE: javax.servlet -> jakarta.servlet namespace change
   */
  @GetMapping("/security/sessions")
  public Map<String, Object> sessionInfo() {
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("activeSessionCount", securityFilter.getActiveSessionCount());
    result.put("filterType", "javax.servlet.Filter (requires migration to jakarta.servlet)");
    return result;
  }

  /**
   * Reflection-based API calls that hide deprecated usage from static analysis
   * MIGRATION CHALLENGE: Requires manual code review and runtime testing
   */
  @GetMapping("/reflection/test")
  public Map<String, Object> reflectionTest() {
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("threadStop", reflectiveApiCaller.callDeprecatedThreadStop());
    result.put("threadDestroy", reflectiveApiCaller.callDeprecatedThreadDestroy());
    result.put("unsafeAccess", reflectiveApiCaller.accessUnsafeDynamically());
    result.put("aclCheck", reflectiveApiCaller.createAclDynamically());
    result.put("methodHandles", reflectiveApiCaller.useMethodHandles());
    return result;
  }

  /**
   * Complex reflection chain that requires deep analysis
   */
  @GetMapping("/reflection/chain")
  public String reflectionChain() {
    return reflectiveApiCaller.complexReflectiveChain();
  }

  /**
   * Complex JAXB marshalling with custom adapters
   * MIGRATION CHALLENGE: javax.xml.bind -> jakarta.xml.bind with custom adapters
   */
  @GetMapping(value = "/xml/transaction", produces = MediaType.APPLICATION_XML_VALUE)
  public String complexJaxbDemo() {
    try {
      ComplexJaxbProcessor.Transaction tx = jaxbProcessor.createSampleTransaction();
      return jaxbProcessor.marshalWithCustomConfig(tx);
    } catch (JAXBException e) {
      return "<error>JAXB marshalling failed: " + e.getMessage() + "</error>";
    }
  }

  /**
   * JAXB with callbacks and listeners
   */
  @GetMapping(value = "/xml/transaction-callbacks", produces = MediaType.APPLICATION_XML_VALUE)
  public String jaxbWithCallbacks() {
    try {
      ComplexJaxbProcessor.Transaction tx = jaxbProcessor.createSampleTransaction();
      return jaxbProcessor.marshalWithCallbacks(tx);
    } catch (JAXBException e) {
      return "<error>JAXB callback marshalling failed: " + e.getMessage() + "</error>";
    }
  }
}
