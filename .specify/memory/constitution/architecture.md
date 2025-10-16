# Constitution Architecture Standards

<!--
SYNC IMPACT REPORT (2025-10-16):
- File: architecture.md
- Version: 1.0.0 → 1.0.0 (initial creation from template)
- Modified Principles: All (initial population)
- Added Sections: Complete architecture standards for Spring Boot REST API
- Removed Sections: None
- Templates Requiring Updates: None (initial setup)
- Follow-up TODOs: None
- Impact: Establishes Spring Boot 2.7.18 architectural patterns
-->

<!--
Section: architecture
Priority: high
Applies to: all projects
Dependencies: [core]
Version: 1.0.0
Last Updated: 2025-10-16
Project: Legacy-Backend-SpringBoot2-Java11
-->

## 1. Architectural Principles

| Principle               | Description                                      | Priority | Implementation                    |
| ----------------------- | ------------------------------------------------ | -------- | --------------------------------- |
| **Design Pattern**      | Layered Architecture (Controller-Service-Repository) | MUST     | Standard Spring Boot MVC pattern  |
| Service Responsibility  | Single Responsibility - one business concern per service | MUST     | One concern per service class     |
| State Management        | Stateless services with database persistence     | MUST     | Stateless Spring beans            |
| Component Separation    | Clear boundaries between layers (no cross-layer access) | MUST     | Package structure enforcement     |
| Data Access Pattern     | Repository pattern with Spring Data JPA          | MUST     | JpaRepository interfaces          |
| Performance Constraints | API response time <500ms for 95th percentile     | SHOULD   | Response time SLO                 |

---

## 2. Service Architecture

| Component        | Responsibility                                 | Pattern                        | Notes                             |
| ---------------- | ---------------------------------------------- | ------------------------------ | --------------------------------- |
| **Handlers**     | REST endpoints, request/response transformation | @RestController with @RequestMapping | Entry point, thin logic           |
| **Services**     | Business logic, orchestration, validation      | @Service annotation            | Business logic layer              |
| **Repositories** | Data access, CRUD operations                   | Spring Data JpaRepository      | Data access abstraction           |
| **Models/DTOs**  | Data transfer objects and JPA entities         | POJO with JPA annotations      | Separate DTOs from entities       |
| **Validators**   | Input validation, business rule validation     | @Valid, custom validators      | JSR-303 Bean Validation           |
| **Middleware**   | Filters, interceptors, exception handlers      | Filter, @ControllerAdvice      | Cross-cutting concerns            |

### Service Flow Pattern

| Step | Layer      | Action                            | Validation                     |
| ---- | ---------- | --------------------------------- | ------------------------------ |
| 1    | Controller | Parse request, validate structure | @Valid annotation, @RequestBody |
| 2    | Service    | Execute business logic            | Business rules, authorization  |
| 3    | Repository | Persist/retrieve data             | JPA entity validation          |
| 4    | Controller | Format response, handle errors    | ResponseEntity<T> wrapper      |

### CRUD Operations Standard

| HTTP Method | Route Pattern        | Service Method           | Expected Behavior                  |
| ----------- | -------------------- | ------------------------ | ---------------------------------- |
| POST        | `/api/resource`      | createResource(dto)      | 201 Created with Location header   |
| GET         | `/api/resource/:id`  | getResourceById(id)      | 200 OK or 404 Not Found            |
| GET         | `/api/resource`      | listResources(params)    | 200 OK with list (empty if none)   |
| PATCH       | `/api/resource/:id`  | updateResource(id, dto)  | 200 OK or 404 Not Found            |
| DELETE      | `/api/resource/:id`  | deleteResource(id)       | 204 No Content or 404 Not Found    |

---

## 3. Database Design Standards

| Guideline             | Requirement                                       | Priority    | When Required                       |
| --------------------- | ------------------------------------------------- | ----------- | ----------------------------------- |
| **Primary Keys**      | Auto-generated Long IDs with @GeneratedValue      | MUST        | All JPA entities                    |
| Index Design          | Add @Index for frequently queried fields          | SHOULD      | Query performance optimization      |
| **Table Naming**      | Uppercase table names (e.g., CUSTOMER)            | MUST        | Database naming convention          |
| Column Naming         | Uppercase column names (e.g., FIRST_NAME)         | MUST        | Database naming convention          |
| **Entity Validation** | Use @NotNull, @Size, @Email for entity constraints | MUST        | Data integrity                      |
| Relationship Mapping  | Use @OneToMany, @ManyToOne with proper cascade    | MUST        | JPA relationships                   |
| **Schema Management** | Schema defined in schema.sql, data in data.sql    | MUST        | H2 initialization                   |
| Schema Versioning     | Version schema changes in migration scripts       | SHOULD      | Future database migration           |

### Database Prohibitions (WON'T)

- Native SQL queries without justification (prefer JPQL)
- Lazy loading without explicit fetch strategy
- Bidirectional relationships without careful cascade configuration
- Auto-DDL in production (spring.jpa.hibernate.ddl-auto=none for prod)
- Missing @Transactional on service methods that modify data

---

## 4. API Design Standards

| Standard Area           | Requirement                                              | Priority | Validation              |
| ----------------------- | -------------------------------------------------------- | -------- | ----------------------- |
| **Security Headers**    | CORS, X-Content-Type-Options, X-Frame-Options            | MUST     | Spring Security config  |
| **CORS Policy**         | Configured per environment (restrictive for production)  | MUST     | WebMvcConfigurer        |
| **Rate Limiting**       | Not implemented (future enhancement)                     | COULD    | Future requirement      |
| **Request Size Limits** | Default Spring Boot limits (2MB for multipart)           | MUST     | application.properties  |
| **Error Responses**     | Consistent JSON error format with status, message, path  | MUST     | @ControllerAdvice       |
| Error Localization      | English error messages (i18n support optional)           | COULD    | Future enhancement      |
| **Authentication**      | Not implemented in baseline (demo project)               | N/A      | Future security layer   |
| Token Validation        | Not applicable (no auth in baseline)                     | N/A      | Future security layer   |
| **Authorization**       | ACL-based authorization (legacy java.security.acl)       | MUST     | CustomAclManager        |
| **Input Validation**    | JSR-303 Bean Validation with @Valid                      | MUST     | Validator framework     |
| Output Sanitization     | JSON serialization via Jackson (default escaping)        | MUST     | Jackson configuration   |
| **Content-Type Check**  | Accept JSON and XML (application/json, application/xml)  | MUST     | @RequestMapping produces |
| **API Versioning**      | URL path versioning: /api/v1/ (currently implicit /api/) | SHOULD   | Future versioning       |
| **Throttling**          | Not implemented (future enhancement)                     | COULD    | Future requirement      |

### Legacy API Patterns (Migration Concerns)

| Pattern                            | Current Implementation         | Migration Impact                         |
| ---------------------------------- | ------------------------------ | ---------------------------------------- |
| javax.servlet.http.HttpServletRequest | Used in controllers            | Migrate to jakarta.servlet               |
| javax.xml.bind JAXB marshalling    | XmlCustomer, ComplexJaxbProcessor | Migrate to jakarta.xml.bind              |
| Custom servlet Filter              | LegacySecurityFilter           | Migrate to jakarta.servlet.Filter        |
| java.security.acl authorization    | CustomAclManager               | Redesign with Spring Security or custom RBAC |

---

## 5. Security Architecture

| Security Layer            | Requirement                                           | Priority | Implementation                  |
| ------------------------- | ----------------------------------------------------- | -------- | ------------------------------- |
| **Defense in Depth**      | Multiple validation layers (controller, service, data) | MUST     | Layered validation              |
| **Zero Trust**            | Validate all inputs regardless of source              | MUST     | Input validation at entry       |
| Network Segmentation      | Not applicable (single application)                   | N/A      | No network architecture         |
| **Identity Verification** | Legacy ACL-based verification                         | MUST     | CustomAclManager                |
| **Security Monitoring**   | Logging of security events (ACL checks, filter actions) | MUST     | SLF4J logging                   |
| Threat Detection          | Manual log review (no automated detection)            | COULD    | Future enhancement              |
| **Audit Logging**         | Log all ACL permission checks and denials             | MUST     | Security event logging          |
| Security Metrics          | Not implemented (future enhancement)                  | COULD    | Future metrics collection       |
| **Encryption**            | HTTPS in production (TLS 1.2+)                        | MUST     | Server configuration            |
| Key Management            | Not applicable (no encryption keys in baseline)       | N/A      | Future key management           |

### Security Anti-Patterns (Intentional for Migration Testing)

| Anti-Pattern                       | Location                      | Risk Level | Migration Action                     |
| ---------------------------------- | ----------------------------- | ---------- | ------------------------------------ |
| java.security.acl (removed JDK 17) | CustomAclManager              | CRITICAL   | Complete architectural redesign      |
| Reflection-based API access        | ReflectiveApiCaller           | HIGH       | Runtime discovery of hidden failures |
| Thread.stop() usage                | LegacyThreadService           | HIGH       | Cooperative cancellation required    |
| sun.misc.Unsafe access             | UnsafeDemo                    | MEDIUM     | Replace with VarHandle               |
| Deprecated servlet APIs            | LegacySecurityFilter          | MEDIUM     | Namespace migration to jakarta       |

---

## 6. Asynchronous Processing Standards

| Standard                  | Requirement                                   | Priority | Notes                              |
| ------------------------- | --------------------------------------------- | -------- | ---------------------------------- |
| **@Async Methods**        | Must return void, Future, or CompletableFuture | MUST     | Spring Framework requirement       |
| Async Configuration       | @EnableAsync with custom executor             | MUST     | AsyncConfig class                  |
| **Exception Handling**    | Use AsyncUncaughtExceptionHandler             | MUST     | Async exception logging            |
| Thread Pool Configuration | Custom ThreadPoolTaskExecutor                 | MUST     | Configured in AsyncConfig          |
| **Async Method Naming**   | Prefix with 'async' or suffix with 'Async'    | SHOULD   | Naming convention                  |

### Invalid @Async Patterns (Legacy Code for Testing)

| Invalid Pattern                       | Example Method              | Why Invalid                          |
| ------------------------------------- | --------------------------- | ------------------------------------ |
| Returns String                        | invalidAsyncReturn()        | Spring 6 rejects non-Future returns  |
| Returns custom object (UserData)      | invalidAsyncCustomObject()  | Not Future/CompletableFuture         |
| Returns primitive (int)               | invalidAsyncPrimitive()     | Not Future/CompletableFuture         |
| Returns List<String>                  | invalidAsyncList()          | Not Future/CompletableFuture         |
| Returns Map<String, List<UserData>>   | invalidAsyncComplexGenerics() | Not Future/CompletableFuture         |

**Valid Pattern**: `CompletableFuture<String> validAsyncReturn()` - Returns CompletableFuture wrapper

---

## 7. Package Structure Standards

| Package                     | Purpose                          | Allowed Dependencies                |
| --------------------------- | -------------------------------- | ----------------------------------- |
| `controller`                | REST endpoints                   | service, domain (DTOs only)         |
| `service`                   | Business logic                   | repository, domain                  |
| `repo` (repository)         | Data access                      | domain (entities only)              |
| `domain`                    | Entities and DTOs                | None (pure data classes)            |
| `config`                    | Spring configuration             | Any (configuration layer)           |
| `legacy`                    | Deprecated code for migration    | Any (intentionally problematic)     |
| `security`                  | Security components              | domain, service                     |
| `servlet`                   | Servlet filters                  | javax.servlet (migration required)  |
| `util`                      | Utility classes                  | Minimal dependencies                |
| `xml`                       | JAXB processing                  | javax.xml.bind (migration required) |

### Architecture Prohibitions

- Controllers directly accessing repositories (must go through services)
- Domain entities containing business logic (anemic domain model acceptable for CRUD)
- Circular dependencies between packages
- Direct database access from controllers
- Service-to-service circular dependencies
