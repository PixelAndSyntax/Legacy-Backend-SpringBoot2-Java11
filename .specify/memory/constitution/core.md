# Constitution Core Standards

<!--
SYNC IMPACT REPORT (2025-10-16):
- File: core.md
- Version: 1.0.0 → 1.1.0 (removed database and CI/CD references)
- Modified Principles: Technology Stack, Enforcement sections updated
- Added Sections: None
- Removed Sections: Database standards, CI/CD validation
- Templates Requiring Updates: None
- Follow-up TODOs: None
- Impact: Focused on Java 11 and Spring Boot 2 best practices only
-->

<!--
Section: core
Priority: critical
Applies to: all projects
Version: 1.1.0
Last Updated: 2025-10-16
Project: Legacy-Backend-SpringBoot2-Java11
-->

## 1. Technology Stack Standards

| Component              | Requirement                                     | Priority | Notes                                          |
| ---------------------- | ----------------------------------------------- | -------- | ---------------------------------------------- |
| **Runtime**            | Java 11 (Azul Zulu 11.0.20)                     | MUST     | Legacy baseline - migration target is Java 17  |
| Runtime Security       | Security Manager disabled by default            | MUST     | Deprecated in Java 11, removed in Java 17      |
| Runtime Optimization   | G1GC recommended for heap >4GB                  | SHOULD   | Performance best practice                      |
| Runtime Monitoring     | JMX enabled for production monitoring           | SHOULD   | Standard JVM observability                     |
| Runtime Enhancement    | Flight Recorder for profiling                   | COULD    | Performance diagnostics                        |
| **Language**           | Java 11                                         | MUST     | Primary language - strict version enforcement  |
| Language Strictness    | Compiler warnings enabled (-Xlint:all)          | MUST     | Type safety and deprecation awareness          |
| Language Linting       | Maven Checkstyle plugin required                | MUST     | Code quality enforcement                       |
| Language Best Practice | Effective Java 3rd Edition patterns             | SHOULD   | Joshua Bloch recommendations                   |
| Language Documentation | Javadoc for public APIs                         | SHOULD   | API documentation standard                     |
| Language Optional      | JDK 11 var keyword for local variables          | COULD    | Readability improvement                        |
| **Compute Platform**   | Spring Boot 2.7.18                              | MUST     | All deployments - migration target is Boot 3.x |
| Compute Security       | Spring Security 5.x baseline                    | MUST     | Security compliance                            |
| Compute Config         | Maven 3.9.3+ with .mvn/ directory configuration | MUST     | Standard configuration in .mvn/settings.xml    |
| Compute Optimization   | Spring Boot actuator for metrics                | SHOULD   | Performance tuning                             |
| Compute Monitoring     | Actuator /health and /metrics endpoints         | SHOULD   | Health checks                                  |

### Technology Prohibitions (WON'T without RFC)

- Alternative runtimes without formal RFC approval (no OpenJDK variants without justification)
- JDK 17+ until migration complete (intentionally constrained to Java 11 baseline)
- Use of `--add-opens` or `--add-exports` without documented justification
- Reflective access to JDK internals (sun.misc.Unsafe requires explicit approval)
- Alternative compute platforms (must remain Spring Boot 2.7.18 until migration)
- Use of Spring Boot 3.x dependencies prematurely
- Spring Native or GraalVM without RFC

---

## 2. Coding Standards

| Area               | Standard                                                     | Enforcement | Validation          |
| ------------------ | ------------------------------------------------------------ | ----------- | ------------------- |
| **Language**       | Java 11 source/target compatibility                          | MUST        | Maven compiler      |
| **Type Safety**    | Strict compilation with `-Xlint:all -Werror` for deprecations | MUST        | Compile-time        |
| **Async Patterns** | @Async methods MUST return void, Future, CompletableFuture   | MUST        | Code review         |
| **Modularity**     | Package-by-feature organization                              | MUST        | Architecture review |
| **Error Handling** | Never swallow exceptions, log with context                   | MUST        | Code review         |
| **Logging**        | SLF4J with Logback; structured logging with correlation IDs  | MUST        | Automated scanning  |
| **Secrets**        | No hardcoded credentials, use environment variables          | MUST        | Secret scanning     |
| **Validation**     | Validate all REST inputs with @Valid and custom validators   | MUST        | Security review     |
| **DTOs/Models**    | Suffix DTOs with DTO, Entities remain plain (e.g., Customer) | MUST        | Code review         |

### Error Handling Example

```java
try {
    CustomerDto result = customerService.processCustomer(customerId);
    return ResponseEntity.ok(result);
} catch (CustomerNotFoundException e) {
    logger.error("Customer not found: {}", customerId, e);
    return ResponseEntity.notFound().build();
} catch (Exception e) {
    logger.error("Unexpected error processing customer: {}", customerId, e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
}
```

### Core Requirements

- **Logging**: Structured format with SLF4J, correlation ID in MDC, no secrets in logs, consistent field names
- **Secrets**: No plaintext in code/properties/logs/errors, use environment variables or secrets manager
- **Validation**: Validate/sanitize all external inputs, JSR-303 Bean Validation (@Valid, @NotNull, etc.)
- **DTOs**: Clear naming conventions, immutability preferred (final fields), validation annotations

### Legacy Code Patterns (Intentional for Migration Testing)

| Pattern                                      | Location                      | Status       | Migration Action Required                  |
| -------------------------------------------- | ----------------------------- | ------------ | ------------------------------------------ |
| javax.security.cert usage                    | legacy/CertParser.java        | DEPRECATED   | Replace with java.security.cert            |
| javax.xml.bind (JAXB)                        | domain/XmlCustomer.java       | DEPRECATED   | Migrate to jakarta.xml.bind                |
| java.security.acl usage                      | security/CustomAclManager.java | REMOVED JDK17 | Redesign with Spring Security ACL or RBAC  |
| Thread.stop()/destroy()                      | legacy/LegacyThreadService.java | REMOVED JDK17 | Cooperative cancellation with interrupts   |
| sun.misc.Unsafe                              | legacy/UnsafeDemo.java        | RESTRICTED   | Replace with VarHandle or safe alternatives |
| java.applet.Applet                           | legacy/AppletRef.java         | REMOVED JDK17 | Remove entirely                            |
| Invalid @Async return types (String, List)   | service/AsyncService.java     | INVALID      | Return CompletableFuture<T> or void        |
| Reflection-based deprecated API calls        | util/ReflectiveApiCaller.java | HIDDEN       | Runtime testing to discover failures       |
| Complex JAXB with custom adapters/listeners  | xml/ComplexJaxbProcessor.java | COMPLEX      | Test thoroughly after jakarta migration    |
| javax.servlet Filter implementation          | servlet/LegacySecurityFilter.java | DEPRECATED   | Migrate to jakarta.servlet                 |

---

## 3. API Versioning Standards

| Versioning Aspect | Requirement                                         | Priority | Notes                                  |
| ----------------- | --------------------------------------------------- | -------- | -------------------------------------- |
| **Strategy**      | URL path versioning: /api/v1/, /api/v2/             | MUST     | RESTful convention                     |
| Version support   | Support previous version during transition (6 mo)   | MUST     | Minimum 6 months overlap               |
| Breaking changes  | Document all breaking changes in CHANGELOG.md       | MUST     | In CHANGELOG and API docs              |
| Deprecation       | Deprecate endpoints before removal (3 months notice) | MUST     | Minimum 3 months notice with @Deprecated |
| Migration guides  | Provide migration guides for major version changes  | SHOULD   | With code examples and rationale       |

---

## 4. Enforcement and Validation

| Standard Area      | Enforcement Level | Validation Method           | Automated | Frequency    |
| ------------------ | ----------------- | --------------------------- | --------- | ------------ |
| Language Standards | Mandatory         | Maven compiler + Checkstyle | Yes       | Every commit |
| Type Safety        | Mandatory         | Compile-time warnings       | Yes       | Every commit |
| Error Handling     | Mandatory         | Code review                 | Partial   | Every PR     |
| Logging            | Mandatory         | Logback configuration       | Yes       | Every commit |
| Secrets            | Mandatory         | Manual code review          | Partial   | Every PR     |
| Input Validation   | Mandatory         | Security review + tests     | Partial   | Per PR       |
| Versioning         | Mandatory         | Maven enforcer plugin       | Yes       | Every build  |
| Architecture       | Mandatory         | Architecture review         | No        | Per feature  |

## 5. Dependency Management Standards

| Requirement               | Description                                      | Priority | Validation         |
| ------------------------- | ------------------------------------------------ | -------- | ------------------ |
| **Dependency Versions**   | Use Spring Boot parent POM version management    | MUST     | Maven dependency   |
| BOM Management            | Import dependency BOMs for consistent versions   | MUST     | Maven configuration |
| **SNAPSHOT Prohibition**  | No SNAPSHOT dependencies in main/master branch   | MUST     | Manual review           |
| Dependency Vulnerability  | Scan dependencies for CVEs before release        | SHOULD   | OWASP Dependency   |
| **License Compliance**    | Only approved licenses (Apache 2.0, MIT, BSD)    | MUST     | Manual review    |
| Transitive Dependencies   | Review and justify all transitive dependencies   | SHOULD   | Dependency tree    |
| **Minimal Dependencies**  | Prefer built-in solutions over external libraries | SHOULD   | Architecture review |

### Legacy Dependencies (Intentional for Migration Testing)

| Dependency                               | Version   | Purpose                             | Migration Required                |
| ---------------------------------------- | --------- | ----------------------------------- | --------------------------------- |
| spring-boot-starter-parent               | 2.7.18    | Spring Boot baseline                | Upgrade to 3.x                    |
| javax.servlet-api (transitive)           | 4.x       | Servlet API                         | Replace with jakarta.servlet-api  |
| javax.xml.bind (jaxb-api)                | 2.3.x     | JAXB XML binding                    | Replace with jakarta.xml.bind-api |
| javax.persistence (transitive via Hibernate) | 2.2.x     | JPA annotations                     | Replace with jakarta.persistence  |
| commons-beanutils                        | 1.9.4     | Bean utilities (javax.* based)      | Update or replace                 |
| hibernate-validator                      | 6.2.5     | javax.validation.* based            | Update to jakarta.validation      |
| jackson-module-jaxb-annotations          | 2.13.5    | JAXB annotations support            | Update for jakarta compatibility  |
| apache-cxf-rt-frontend-jaxrs             | 3.4.10    | JAX-RS implementation (javax.ws.rs) | Update or replace                 |

## 6. Build and Deployment Standards

| Requirement           | Description                                   | Priority | Validation    |
| --------------------- | --------------------------------------------- | -------- | ------------- |
| **Build Tool**        | Maven 3.9.3+ with .mvn/settings.xml           | MUST     | Maven configuration |
| Build Reproducibility | Consistent builds with .mvn/maven.config      | MUST     | Manual validation |
| **JVM Configuration** | Standardized JVM args in .mvn/jvm.config      | MUST     | Build config  |
| Clean Builds          | `mvn clean install` must always succeed       | MUST     | Manual verification      |
| **Test Execution**    | All tests must pass before commit             | MUST     | Developer discipline    |
| Build Warnings        | Address all compiler warnings                 | SHOULD   | Code review   |
| **Packaging**         | JAR packaging for Spring Boot executable      | MUST     | Maven config  |

````
