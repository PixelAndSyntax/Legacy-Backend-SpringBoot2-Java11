# Constitution Security Standards

<!--
SYNC IMPACT REPORT (2025-10-16):
- File: security.md
- Version: 1.0.0 → 1.1.0 (removed database and CI/CD references)
- Modified Principles: Data Protection, Secret Management, Vulnerability Management sections updated
- Added Sections: None
- Removed Sections: Database-specific security requirements, CI pipeline references
- Templates Requiring Updates: None
- Follow-up TODOs: Document ACL migration strategy after Spring Security adoption
- Impact: Focus on application-layer security only
-->

<!--
Section: security
Priority: critical
Applies to: all projects (especially backend)
Dependencies: [core]
Version: 1.1.0
Last Updated: 2025-10-16
Project: Legacy-Backend-SpringBoot2-Java11
-->

## 1. Core Security Principles

| Principle              | Requirement                              | Priority | Enforcement         |
| ---------------------- | ---------------------------------------- | -------- | ------------------- |
| **Least Privilege**    | Grant minimal permissions required       | MUST     | ACL configuration   |
| **Zero Trust**         | Verify every access request              | MUST     | CustomAclManager    |
| **Defense in Depth**   | Multiple validation layers               | MUST     | Filter + ACL + Service |
| **Fail Secure**        | Default deny on authorization failures   | MUST     | ACL entry defaults  |
| **Complete Mediation** | Check every access attempt               | MUST     | Filter intercepts all |

**⚠️ MIGRATION WARNING**: Core security implementation uses `java.security.acl` package which is **removed in Java 17**. Complete architectural redesign required.

---

## 2. Authentication & Authorization

### Current Implementation (Legacy ACL)

| Security Control      | Requirement                                  | Priority | Validation              |
| --------------------- | -------------------------------------------- | -------- | ----------------------- |
| **Authentication**    | Not implemented (demo project baseline)      | N/A      | Future security layer   |
| **Authorization**     | ACL-based with java.security.acl package     | MUST     | CustomAclManager        |
| ACL Management        | Dynamic ACL with owner, positive/negative permissions | MUST     | CustomAclManager        |
| Permission Checks     | Check permissions before resource access     | MUST     | ACL.checkPermission()   |
| Session Tracking      | Custom session tracking in servlet filter    | MUST     | LegacySecurityFilter    |

### Legacy ACL Architecture (Requires Migration)

| Component                  | Current Implementation       | Java 17 Status | Migration Required                |
| -------------------------- | ---------------------------- | -------------- | --------------------------------- |
| `java.security.acl.Acl`    | CustomAclManager implements  | REMOVED        | Redesign with Spring Security ACL |
| `java.security.acl.AclEntry` | Permission entries          | REMOVED        | Custom permission model           |
| `java.security.acl.Permission` | Custom permissions        | REMOVED        | Spring Security authorities       |
| `java.security.acl.Owner`  | ACL ownership                | REMOVED        | Spring Security ownership         |

**Migration Options**:
1. **Spring Security ACL**: Adopt Spring Security ACL module (complex but supported)
2. **Custom RBAC**: Design custom role-based access control
3. **External Authorization**: Use external service (e.g., OPA, Keycloak)

---

## 3. Data Protection

| Protection Type           | Requirement                                | Priority | Implementation               |
| ------------------------- | ------------------------------------------ | -------- | ---------------------------- |
| **Encryption in Transit** | HTTPS recommended for production           | MUST     | Server configuration         |
| **PII Handling**          | No PII in logs or error messages           | MUST     | Log review                   |
| Data Minimization         | Collect only necessary data                | MUST     | API design                   |
| **Data Classification**   | Classify data by sensitivity level         | SHOULD   | Security policy              |
| Secure Random Generation  | Use `SecureRandom` for all random values   | MUST     | Cryptographic operations     |

---

## 4. Input Validation & Output Sanitization

| Security Control         | Requirement                                   | Priority | Protection Against   |
| ------------------------ | --------------------------------------------- | -------- | -------------------- |
| **Input Validation**     | JSR-303 Bean Validation on all REST inputs    | MUST     | Injection attacks    |
| @Valid Annotation        | Use @Valid on @RequestBody parameters         | MUST     | Invalid data         |
| Custom Validators        | Implement ConstraintValidator for business rules | SHOULD   | Business logic bypass |
| **Output Encoding**      | Jackson JSON serialization (default escaping) | MUST     | XSS prevention       |
| XML Output               | JAXB marshalling (safe by default)            | MUST     | XML injection        |
| **Type Validation**      | Strict type checking on all inputs            | MUST     | Type confusion       |

### Security Testing Requirements

| Test Type           | Requirement                          | Priority |
| ------------------- | ------------------------------------ | -------- |
| Invalid Input Tests | Test null, empty, malformed inputs   | MUST     |
| Boundary Tests      | Test min/max values                  | SHOULD   |
| Type Mismatch       | Test wrong data types                | SHOULD   |
| Exception Handling  | Verify secure error messages         | MUST     |

---

## 5. Secret Management

| Secret Type              | Requirement                              | Priority | Storage Method           |
| ------------------------ | ---------------------------------------- | -------- | ------------------------ |
| **API Keys**             | Store in environment variables           | MUST     | External configuration   |
| **Configuration**        | Environment variables for sensitive config | MUST     | Spring @Value or @ConfigurationProperties |
| **Tokens**               | Never hardcode authentication tokens     | MUST     | External configuration   |

### Secret Prohibitions (WON'T)

- Never log credentials or tokens
- Never commit secrets to version control
- Never store secrets in plaintext in application.properties
- Never expose secrets in error messages
- Never transmit secrets in URLs

---

## 6. Security Logging & Monitoring

| Event Type                 | Logging Requirement                           | Priority | Retention Period |
| -------------------------- | --------------------------------------------- | -------- | ---------------- |
| **Authorization Failures** | Log all ACL permission denials                | MUST     | Application logs |
| Permission Checks          | Log ACL checkPermission() results             | SHOULD   | Debug logging    |
| **Filter Actions**         | Log servlet filter security decisions         | MUST     | Application logs |
| Session Tracking           | Log session creation/destruction              | SHOULD   | Application logs |
| **Exceptions**             | Log all security-related exceptions           | MUST     | Error logs       |

### Security Logging Implementation

**LegacySecurityFilter**: Logs all HTTP requests with session tracking
**CustomAclManager**: Logs ACL permission checks and denials

### Logging Prohibitions (WON'T)

- Never log user passwords or credentials
- Never log full authentication tokens
- Never log sensitive PII without justification
- Never log cryptographic keys or secrets

---

## 7. Network Security

| Control                  | Requirement                              | Priority | Implementation       |
| ------------------------ | ---------------------------------------- | -------- | -------------------- |
| **HTTPS**                | Required for production deployments      | MUST     | Server configuration |
| **TLS Configuration**    | Minimum TLS 1.2 (prefer TLS 1.3)         | MUST     | Server configuration |
| **CORS Policy**          | Restrictive CORS configuration           | MUST     | WebMvcConfigurer     |
| CORS Origins             | Whitelist specific origins (no wildcards) | SHOULD   | Production config    |
| **Firewall Rules**       | Not applicable (application level)       | N/A      | Infrastructure       |

### CORS Configuration Example

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins("https://trusted-domain.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

---

## 8. Vulnerability Management

| Activity                | Requirement                                | Frequency        | Priority |
| ----------------------- | ------------------------------------------ | ---------------- | -------- |
| **Dependency Scanning** | Scan Maven dependencies for CVEs           | Every build      | MUST     |
| OWASP Dependency Check  | Use OWASP Dependency-Check Maven plugin    | Manual review    | SHOULD   |
| Vulnerability Patching  | Update vulnerable dependencies promptly    | Within 30 days   | MUST     |
| **Security Audits**     | Review security architecture               | Before migration | SHOULD   |

### Known Legacy Security Issues

| Component                     | Security Issue                          | Risk Level | Migration Action                      |
| ----------------------------- | --------------------------------------- | ---------- | ------------------------------------- |
| java.security.acl             | Package removed in Java 17              | CRITICAL   | Complete architectural redesign       |
| javax.servlet                 | Deprecated (jakarta migration required) | MEDIUM     | Namespace migration                   |
| Thread.stop()                 | Unsafe thread termination               | HIGH       | Cooperative cancellation              |
| sun.misc.Unsafe               | Direct memory access                    | MEDIUM     | Replace with VarHandle                |
| Reflection-based API calls    | Hidden from static analysis             | MEDIUM     | Runtime testing required              |

---

## 9. Servlet Filter Security

### LegacySecurityFilter Implementation

| Feature                  | Implementation                      | Priority | Notes                           |
| ------------------------ | ----------------------------------- | -------- | ------------------------------- |
| **Request Interception** | Intercepts all requests via Filter  | MUST     | javax.servlet.Filter            |
| Session Tracking         | Custom session tracking             | MUST     | HttpServletRequest.getSession() |
| Request Wrapping         | HttpServletRequestWrapper for modification | SHOULD   | Advanced filtering              |
| **Security Headers**     | Add security headers to responses   | SHOULD   | X-Content-Type-Options, etc.    |

### Migration Requirements

| Current (javax)                 | Target (jakarta)                   | Change Required           |
| ------------------------------- | ---------------------------------- | ------------------------- |
| javax.servlet.Filter            | jakarta.servlet.Filter             | Import change             |
| javax.servlet.FilterChain       | jakarta.servlet.FilterChain        | Import change             |
| javax.servlet.http.HttpServletRequest | jakarta.servlet.http.HttpServletRequest | Import change             |
| javax.servlet.http.HttpServletResponse | jakarta.servlet.http.HttpServletResponse | Import change             |
| FilterRegistrationBean<Filter>  | FilterRegistrationBean<jakarta.servlet.Filter> | Type parameter change     |

---

## 10. ACL Security Model (Legacy)

### Custom ACL Implementation

**CustomAclManager** implements complete ACL security layer:

| ACL Feature              | Implementation                           | Priority |
| ------------------------ | ---------------------------------------- | -------- |
| **Ownership**            | ACL owner with full permissions          | MUST     |
| Positive Permissions     | Explicitly granted permissions           | MUST     |
| Negative Permissions     | Explicitly denied permissions (override) | MUST     |
| Permission Checking      | checkPermission(principal, permission)   | MUST     |
| Dynamic ACL Entries      | Runtime permission management            | MUST     |

### ACL Permission Model

```java
// Check if principal has permission
boolean hasPermission = acl.checkPermission(principal, permission);

// Add positive permission
AclEntry entry = acl.addEntry(principal, permission);

// Add negative permission (denial)
entry.setNegativePermissions();
```

### Migration Complexity

**Why ACL Migration is Critical**:
1. No direct API replacement in Java 17
2. Business logic embedded in ACL implementation
3. Permission cascading and inheritance logic
4. Negative permissions (denial) pattern
5. Dynamic permission management at runtime

**Recommended Migration Path**:
1. Analyze all ACL usage patterns
2. Map to Spring Security ACL or custom RBAC
3. Implement equivalent permission model
4. Migrate all checkPermission() calls
5. Comprehensive security testing
6. Performance testing (ACL checks can be expensive)

---

## 11. Security Configuration Best Practices

| Practice                    | Requirement                          | Priority | Enforcement    |
| --------------------------- | ------------------------------------ | -------- | -------------- |
| **Fail Secure**             | Default deny for all authorization   | MUST     | ACL defaults   |
| **Least Privilege**         | Minimal permissions by default       | MUST     | ACL configuration |
| **Audit Trail**             | Log all permission checks            | SHOULD   | Security logs  |
| **Regular Review**          | Review ACL entries periodically      | SHOULD   | Manual review  |
| **Principle of Separation** | Separate authentication from authorization | MUST     | Architecture   |
