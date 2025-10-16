# Legacy-Backend-SpringBoot2-Java11

A **baseline legacy project** using **Spring Boot 2.7.18** with **Java 11**, purposely including APIs and patterns that become problematic when upgrading to **Java 17** and **Spring Boot 3.x**. This project serves as an ideal seed input for automated migration tools (e.g., *spec-kit*) and for practicing manual refactors.

## Technology Stack

- **Java**: 11 (Azul Zulu 11.0.20)
- **Spring Boot**: 2.7.18
- **Build Tool**: Maven 3.9.3+
- **Database**: H2 (in-memory)
- **ORM**: Hibernate (JPA with javax.persistence)
- **XML Binding**: JAXB 2.x (javax.xml.bind)

## What this project demonstrates

### Java APIs: deprecated/removed by Java 17
- `javax.security.cert` used by `legacy.CertParser` — replace with `java.security.cert.X509Certificate` + `CertificateFactory`.
- `javax.xml.bind` (JAXB 2.x) used by `domain.XmlCustomer` and tests — switch to **Jakarta** modules (`jakarta.xml.bind-api`).
- `java.security.acl` referenced by `legacy.SecurityAclDemo` — redesign to app-level RBAC/domain rules or Spring Security ACL.
- `Thread.stop(Throwable)` and `Thread.destroy()` mentioned in `legacy.LegacyThreadService` — adopt cooperative cancellation via interrupts.
- `sun.misc.Unsafe` access in `legacy.UnsafeDemo` — replace with `VarHandle` or safe libraries.
- `java.applet` referenced in `legacy.AppletRef` — remove entirely.

### Spring Boot 2.x → 3.x migration hot-spots
- **`javax.*` → `jakarta.*` namespace change**: `javax.servlet.*` in `LegacyController`, `javax.persistence.*` in `Customer`.
- **`@Async` signatures**: `AsyncService.invalidAsyncReturn()` is *intentionally invalid* for Spring 6/Boot 3; `validAsyncReturn()` shows the correct pattern.
- **JAXB**: Uses `javax.xml.bind` dependencies explicitly in `pom.xml` (because JDK 11 no longer bundles JAXB). In Boot 3, replace with Jakarta.
- **Deprecated properties**: `server.max.http.header.size` in `application.properties` (Boot 3 expects `server.max-http-request-header-size`).
- **SAML2 & other property churn**: Not wired here to keep the sample runnable, but see _Migration Notes_ below.

## Build & Run (Java 11)

**Prerequisites:**
- Java 11 (Azul Zulu 11.0.20 recommended)
- Maven 3.9.3+

**Environment Setup:**
The project is configured with Maven settings in `.mvn/` directory:
- `.mvn/settings.xml` - Maven configuration
- `.mvn/jvm.config` - JVM arguments for Maven builds
- `.mvn/maven.config` - Maven CLI arguments

**Build Commands:**

```bash
# Verify Java version
java -version

# Clean build with tests
export JAVA_HOME=/Users/232435/Library/Java/JavaVirtualMachines/azul-11.0.20/Contents/Home
mvn -s .mvn/settings.xml clean install

# Run tests only
mvn -s .mvn/settings.xml test

# Start the application
mvn -s .mvn/settings.xml spring-boot:run
```

Endpoints:
- `GET /api/hello` — echoes a message and shows `javax.servlet` usage.
- `GET /api/xml/customer` — returns XML via JAXB (javax).
- `GET /api/customers` — reads sample H2 data via JPA (javax.persistence).
- `GET /api/thread/stop-demo` — demonstrates the deprecated thread API.
- `GET /api/unsafe` — shows whether `sun.misc.Unsafe` could be accessed.
- `GET /api/applet` — returns the Applet class name.

## Running unit tests

- **`LegacyControllerTest`**: MockMvc tests for REST endpoints and JAXB output.
- **`AsyncServiceTest`**: Verifies the compliant `CompletableFuture` @Async method.
- **`JaxbTest`**: Marshals an object with `javax.xml.bind`.

Run all:
```bash
export JAVA_HOME=/Users/232435/Library/Java/JavaVirtualMachines/azul-11.0.20/Contents/Home
mvn -s .mvn/settings.xml test
```

**Test Results:** All 5 tests pass successfully ✅

## Recent Fixes Applied

This project has been configured and fixed to build successfully with Java 11:

### 1. Java 11 Configuration
- **Created** `.mvn/settings.xml` with proper Java 11 configuration
- **Created** `.mvn/jvm.config` with appropriate JVM memory settings
- **Created** `.mvn/maven.config` for consistent Maven builds
- **Configured** `pom.xml` with Java 11 compiler settings and version enforcement

### 2. Code Compatibility Fixes
- **Fixed** `CertParser.java`: Updated to use `String.getBytes(Charset)` instead of deprecated `getBytes(String)` method
- **Fixed** `SecurityAclDemo.java`:
  - Updated to use `String.getBytes(StandardCharsets.UTF_8)`
  - Fixed `AclEntry.setPrincipal()` signature to accept `Principal` instead of `AclEntry`

### 3. Database Initialization
- **Created** `src/main/resources/schema.sql` to define the CUSTOMER table structure
- **Ensured** H2 database is properly initialized before running tests
- **Verified** `data.sql` executes successfully after schema creation

### 4. Spring Bean Configuration
- **Added** `@Service` annotation to `LegacyThreadService` class
- **Enabled** proper Spring autowiring for dependency injection in `LegacyController`

### 5. Build Success
- **Result**: Clean build with all 5 tests passing
  - AsyncServiceTest: 1 test ✅
  - LegacyControllerTest: 3 tests ✅
  - JaxbTest: 1 test ✅

## Designed upgrade path to Java 17 + Spring Boot 3.x

1. **Bump versions** in `pom.xml`:
   - Set parent to `org.springframework.boot:spring-boot-starter-parent:3.x` and `java.version` to `17`.
2. **Namespace switch**:
   - Replace imports `javax.servlet.*` → `jakarta.servlet.*`.
   - Replace `javax.persistence.*` → `jakarta.persistence.*`.
   - Replace `javax.xml.bind.*` → `jakarta.xml.bind.*` and dependencies to `jakarta.*`.
3. **API removals**:
   - `javax.security.cert` → replace with `java.security.cert` and `CertificateFactory`.
   - Remove `java.applet` usages.
   - Remove `java.security.acl`; consider Spring Security ACL or domain RBAC.
   - Replace `Thread.stop/destroy` with interrupts + flags (cooperative shutdown).
   - Replace `sun.misc.Unsafe` with `VarHandle`.
4. **@Async**: Change invalid `String` return to `CompletableFuture<String>` (or `void`).
5. **Properties**: rename `server.max.http.header.size` → `server.max-http-request-header-size`; audit others (e.g., `spring.redis.*` to `spring.data.redis.*`).
6. **Re-test**: run unit tests and add more as needed.

## Using spec-kit (or any migration assistant)

- Point the tool to this project root. Ask it to:
  1. Upgrade to **Java 17** and **Spring Boot 3.x**.
  2. Perform the **javax → jakarta** namespace migration.
  3. Propose replacements for removed Java APIs listed above.
  4. Update deprecated **configuration properties**.
- Then **manually review** and complete the non-automatable parts (ACL redesign, Unsafe → VarHandle, thread lifecycle changes).

## Notes

- This sample keeps dependencies light so it compiles and runs on Java 11 without extra flags.
- For SAML2 property changes and broader Boot 3 migration guidance, consult your internal docs or the upstream references.
