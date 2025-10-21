# Constitution Observability Standards

<!--
SYNC IMPACT REPORT (2025-10-16):
- File: observability.md
- Version: 1.0.0 → 1.1.0 (removed database references)
- Modified Principles: Metrics and logging sections updated to remove database layer
- Added Sections: None
- Removed Sections: Database performance metrics, repository logging
- Templates Requiring Updates: None
- Follow-up TODOs: None
- Impact: Focus on application-layer observability only
-->

<!--
Section: observability
Priority: high
Applies to: backend, infrastructure
Dependencies: [core]
Version: 1.1.0
Last Updated: 2025-10-16
Project: Legacy-Backend-SpringBoot2-Java11
-->

## 1. Logging Standards

### Mandatory Log Fields

| Field      | Type   | Format               | Required | Description                    |
| ---------- | ------ | -------------------- | -------- | ------------------------------ |
| timestamp  | string | ISO 8601 (Logback default) | MUST     | Log entry timestamp            |
| level      | enum   | INFO/WARN/ERROR/DEBUG | MUST     | Log severity level             |
| logger     | string | Fully qualified class name | MUST     | Logger name (class)            |
| thread     | string | Thread name          | MUST     | Execution thread               |
| message    | string | Descriptive          | MUST     | Human-readable description     |

### Optional Context Fields

| Field     | Type   | When Required          | Description                     |
| --------- | ------ | ---------------------- | ------------------------------- |
| operation | string | Available              | Method being executed           |
| userId    | string | User context exists    | User identifier (if available)  |
| sessionId | string | Session context exists | HTTP session identifier         |
| requestId | string | HTTP/API context       | Request correlation ID          |
| duration  | number | Operation complete     | Execution time (milliseconds)   |
| outcome   | enum   | Operation complete     | success/failure/timeout         |

### Error-Specific Fields

| Field        | Type   | Required When  | Environment   | Description            |
| ------------ | ------ | -------------- | ------------- | ---------------------- |
| errorCode    | string | level == ERROR | All           | Application error code |
| errorMessage | string | level == ERROR | All           | Error description      |
| exception    | string | level == ERROR | All           | Exception class name   |
| stackTrace   | string | level == ERROR | Dev/Test only | Full stack trace       |

### Logging Patterns

| Pattern         | When                | Example                                                    | Priority |
| --------------- | ------------------- | ---------------------------------------------------------- | -------- |
| Entry Logging   | Method starts       | `log.info("Processing customer request: {}", customerId)`  | SHOULD   |
| Success Logging | Operation completes | `log.info("Customer processed successfully: {}", customerId)` | SHOULD   |
| Error Logging   | Operation fails     | `log.error("Failed to process customer: {}", customerId, ex)` | MUST     |
| Debug Logging   | Detailed tracing    | `log.debug("ACL check result: {}", hasPermission)`         | COULD    |

### Logging Prohibitions (WON'T)

- Never log secrets (passwords, tokens, API keys, PKCE verifiers)
- Never log PII without explicit justification and data classification
- Never use string concatenation for log messages (use {} placeholders)
- Never log sensitive cryptographic material
- Never log full stack traces in production ERROR logs (use separate DEBUG)

---

## 2. Log Implementation Standards

| Requirement            | Description                              | Priority | Validation            |
| ---------------------- | ---------------------------------------- | -------- | --------------------- |
| **Logging Framework**  | SLF4J with Logback implementation        | MUST     | Maven dependencies    |
| Logger Declaration     | `private static final Logger log = LoggerFactory.getLogger(ClassName.class)` | MUST     | Code review           |
| **Environment Config** | Logback configuration in logback-spring.xml | SHOULD   | Spring Boot standard  |
| Test Environment       | Console output with pattern              | MUST     | Test validation       |
| Production Environment | File appender with rotation              | SHOULD   | Production config     |
| Log Levels             | INFO/DEBUG/WARN/ERROR (no TRACE)         | MUST     | Logback configuration |
| **Parameterized Logs** | Use {} placeholders, not string concat   | MUST     | Code review           |

### Logger Usage Examples

```java
// CORRECT: Parameterized logging
log.info("Processing customer: {}", customerId);
log.error("Failed to save customer: {}", customerId, exception);

// INCORRECT: String concatenation
log.info("Processing customer: " + customerId); // Don't do this!

// CORRECT: Multiple parameters
log.info("User {} accessed resource {} with permission {}",
         userId, resourceId, permission);
```

---

## 3. Metrics Standards (Future Enhancement)

### Metric Categories

| Category                | Examples                           | Collection | Priority |
| ----------------------- | ---------------------------------- | ---------- | -------- |
| **Business Metrics**    | Customer creations, API calls      | Not implemented | COULD    |
| **System Metrics**      | Request latency, error rates       | Not implemented | SHOULD   |
| **Performance Metrics** | Async task execution, thread pools | Not implemented | COULD    |

**Note**: This baseline project does not implement metrics collection. Consider Spring Boot Actuator for production deployments.

### Future Metrics Implementation

| Requirement           | Description                       | Priority | Notes                        |
| --------------------- | --------------------------------- | -------- | ---------------------------- |
| **Spring Boot Actuator** | Add actuator dependency         | SHOULD   | Standard metrics framework   |
| Metrics Endpoints     | /actuator/metrics, /actuator/health | SHOULD   | Observability endpoints      |
| **Custom Metrics**    | Micrometer for custom counters    | COULD    | Business metrics             |
| Prometheus Export     | Micrometer Prometheus registry    | COULD    | Metrics aggregation          |

---

## 4. Distributed Tracing Standards (Not Implemented)

| Requirement           | Description                             | Priority | Implementation        |
| --------------------- | --------------------------------------- | -------- | --------------------- |
| **Trace Propagation** | Not implemented in baseline             | N/A      | Future: Spring Cloud Sleuth |
| **Correlation IDs**   | Manual correlation ID generation        | COULD    | Custom implementation |
| Span Creation         | Not implemented                         | N/A      | Future enhancement    |

**Note**: This baseline project does not implement distributed tracing. For production, consider:
- Spring Cloud Sleuth (compatible with Spring Boot 2.7.18)
- OpenTelemetry Java Agent
- Zipkin or Jaeger for trace visualization

---

## 5. Alerting Standards (Not Implemented)

| Alert Type          | Condition                    | Severity | Response Time      |
| ------------------- | ---------------------------- | -------- | ------------------ |
| **Error Rate**      | Not monitored                | N/A      | Future enhancement |
| **Availability**    | Not monitored                | N/A      | Future enhancement |
| **Security Events** | Manual log review            | Manual   | Log-based only     |

**Note**: This is a demonstration project. Production deployments should implement proper alerting.

---

## 6. Application Logging Implementation

### Current Logging Configuration

| Component                  | Logging Behavior                          | Log Level |
| -------------------------- | ----------------------------------------- | --------- |
| **LegacyController**       | Logs all endpoint requests and responses  | INFO      |
| **AsyncService**           | Logs async method execution               | INFO      |
| **CustomAclManager**       | Logs ACL permission checks (verbose)      | DEBUG     |
| **LegacySecurityFilter**   | Logs servlet filter actions               | INFO      |
| **ReflectiveApiCaller**    | Logs reflection-based API calls           | WARN      |
| **ComplexJaxbProcessor**   | Logs JAXB marshalling events              | DEBUG     |
| **LegacyThreadService**    | Logs thread lifecycle events              | WARN      |

### Logging Best Practices by Component

| Component Type | When to Log                          | What to Log                      | Level |
| -------------- | ------------------------------------ | -------------------------------- | ----- |
| **Controller** | Request received                     | Endpoint, method, parameters     | INFO  |
| **Controller** | Response sent                        | Status code, result summary      | INFO  |
| **Controller** | Exception thrown                     | Error details, stack trace       | ERROR |
| **Service**    | Business operation start             | Operation name, key parameters   | DEBUG |
| **Service**    | Business operation complete          | Result summary, duration         | INFO  |
| **Service**    | Business rule violation              | Rule name, violation details     | WARN  |
| **Filter**     | Request intercepted                  | URL, method, session info        | DEBUG |
| **Security**   | Authorization check                  | Principal, resource, permission  | DEBUG |
| **Security**   | Authorization failure                | Denial reason, principal         | WARN  |

---

## 7. Logging Configuration

### Logback Configuration (application.properties)

```properties
# Logging levels
logging.level.root=INFO
logging.level.com.example.legacydemo=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Console pattern (development)
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# File logging (production)
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30
```

### Log Level Guidelines

| Level   | Usage                                      | Examples                              |
| ------- | ------------------------------------------ | ------------------------------------- |
| ERROR   | Errors requiring immediate attention       | Exceptions, failures, data corruption |
| WARN    | Warnings about deprecated usage, anomalies | Deprecated API usage, ACL denials     |
| INFO    | Business events and milestones             | Request processing, async completion  |
| DEBUG   | Detailed diagnostic information            | ACL checks, JAXB marshalling          |
| TRACE   | Very detailed diagnostic (not used)        | SQL parameter binding (Hibernate)     |

---

## 8. Migration Observability Considerations

### Logging During Migration

| Migration Phase       | Logging Focus                              | Priority |
| --------------------- | ------------------------------------------ | -------- |
| Pre-migration         | Document current log behavior              | MUST     |
| Dependency Migration  | Log any new deprecation warnings           | MUST     |
| API Replacement       | Log successful replacements and failures   | MUST     |
| Post-migration        | Verify log output matches expectations     | MUST     |

### Migration-Specific Logging

| Legacy Component               | Migration Logging Requirement              | Priority |
| ------------------------------ | ------------------------------------------ | -------- |
| **java.security.acl**          | Log all ACL operations before removal      | MUST     |
| **Thread.stop()**              | Log thread lifecycle for replacement validation | MUST     |
| **Reflection calls**           | Log all reflective API calls at WARN level | MUST     |
| **javax.xml.bind**             | Log JAXB operations for jakarta comparison | SHOULD   |
| **javax.servlet**              | Log filter actions for jakarta validation  | SHOULD   |

---

## 9. Deprecated Property Logging

### Application Properties with Deprecated Names

The project intentionally uses deprecated Spring Boot 2.x property names for migration testing:

| Deprecated Property (Boot 2)         | New Property (Boot 3)                 | Component       |
| ------------------------------------ | ------------------------------------- | --------------- |
| spring.redis.*                       | spring.data.redis.*                   | Redis config    |
| server.max.http.header.size          | server.max-http-request-header-size   | Server config   |
| spring.security.saml2.*              | Updated SAML2 properties              | Security config |
| management.metrics.export.*          | Updated metrics export properties     | Actuator config |
| logging.pattern.console              | (Unchanged, but validation required)  | Logging config  |
| logging.pattern.file                 | (Unchanged, but validation required)  | Logging config  |

**Migration Action**: Update property names in application.properties and verify logging configuration still works.

---

## 10. Performance and Log Volume Management

| Requirement               | Description                           | Priority | Implementation       |
| ------------------------- | ------------------------------------- | -------- | -------------------- |
| **Log Volume Control**    | Avoid excessive logging in tight loops | MUST     | Code review          |
| Debug Level Production    | Minimize DEBUG logs in production     | SHOULD   | Profile-based config |
| **Async Appenders**       | Use async appenders for high volume   | COULD    | Logback configuration |
| Log Rotation              | Rotate logs by size and age           | SHOULD   | Logback configuration |
| **Archive Old Logs**      | Compress and archive old logs         | SHOULD   | Logback configuration |

### Log Volume Anti-Patterns

| Anti-Pattern                  | Risk                     | Solution                          |
| ----------------------------- | ------------------------ | --------------------------------- |
| Logging in tight loops        | Performance degradation  | Aggregate or sample logs          |
| DEBUG level in production     | Excessive log volume     | Use INFO or WARN in production    |
| Full stack traces at INFO     | Log noise                | Stack traces at DEBUG only        |
| Logging full object graphs    | Performance + PII risk   | Log only essential fields         |
