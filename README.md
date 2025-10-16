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
- `GET /api/security/acl` — **Complex ACL security using java.security.acl (removed in Java 17)**
- `GET /api/security/sessions` — **Servlet filter info using javax.servlet APIs**
- `GET /api/reflection/test` — **Reflection-based deprecated API calls (hidden from static analysis)**
- `GET /api/reflection/chain` — **Complex reflection chain**
- `GET /api/xml/transaction` — **Complex JAXB with custom adapters and marshallers**
- `GET /api/xml/transaction-callbacks` — **JAXB with callbacks and listeners**

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

## 🚨 Migration Challenges - Designed to Require Human Intervention

This project is intentionally designed with complexities that **automated migration tools cannot fully handle**. The following challenges require human analysis and decision-making:

### 1. **Deep java.security.acl Integration** 🔴 CRITICAL
**File**: `security/CustomAclManager.java`

**Challenge**: The entire security layer is built on `java.security.acl` which is completely removed in Java 17.

**Why Automation Fails**:
- No direct replacement API exists
- Requires architectural redesign decisions
- Business logic embedded in ACL implementation
- Complex permission cascading and negative permissions

**Manual Steps Required**:
1. Choose replacement strategy (Spring Security ACL / Custom RBAC / External framework)
2. Redesign permission model
3. Migrate all ACL-based business logic
4. Update security tests
5. Validate equivalent behavior

### 2. **Reflection-Based Deprecated API Usage** 🔴 CRITICAL
**File**: `util/ReflectiveApiCaller.java`

**Challenge**: Uses reflection and MethodHandles to dynamically invoke removed APIs.

**Why Automation Fails**:
- Static analysis cannot detect reflective calls
- Method names are strings, not compile-time references
- Dynamic class loading hides dependencies
- MethodHandle usage further obscures API usage

**Manual Steps Required**:
1. Runtime testing to identify all reflective failures
2. Trace through reflection chains manually
3. Replace each dynamic call with safe alternatives
4. Test with Java 17 to catch runtime errors

### 3. **Complex JAXB Custom Adapters** 🟡 MODERATE
**File**: `xml/ComplexJaxbProcessor.java`

**Challenge**: Extensive custom XmlAdapters, Marshaller configurations, and ValidationEventHandlers.

**Why Automation Fails**:
- Custom adapter logic may behave differently in jakarta.xml.bind
- Marshaller property names and behavior may change
- ValidationEventHandler callbacks need manual verification
- Marshaller.Listener callbacks require testing

**Manual Steps Required**:
1. Migrate ALL imports from javax.xml.bind.* to jakarta.xml.bind.*
2. Update custom XmlAdapter implementations
3. Test marshalling/unmarshalling with complex nested objects
4. Verify ValidationEventHandler behavior
5. Test all Marshaller properties and callbacks

### 4. **Servlet Filter Deep Integration** 🟡 MODERATE
**File**: `servlet/LegacySecurityFilter.java`

**Challenge**: Complex javax.servlet Filter with session tracking, request wrapping, and lifecycle management.

**Why Automation Fails**:
- FilterChain behavior may differ in jakarta.servlet
- SessionTrackingMode configuration changes
- HttpServletRequestWrapper implementation details
- Filter lifecycle and context interactions need verification

**Manual Steps Required**:
1. Migrate javax.servlet.* to jakarta.servlet.*
2. Test filter chain ordering and execution
3. Verify session management behavior
4. Validate request wrapper functionality
5. Check ServletContext attribute handling

### 5. **javax.* → jakarta.* Namespace Changes** 🟡 MODERATE
**Files**: Multiple (Controller, Filter, JPA entities, JAXB classes)

**Challenge**: Pervasive use of javax.* packages throughout the codebase.

**Affected Namespaces**:
- `javax.servlet.*` → `jakarta.servlet.*`
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.xml.bind.*` → `jakarta.xml.bind.*`
- `javax.validation.*` → `jakarta.validation.*`

**Manual Steps Required**:
1. Global find/replace with verification
2. Update ALL import statements
3. Verify binary compatibility with jakarta dependencies
4. Test all affected functionality
5. Check third-party library compatibility

### 6. **FilterRegistrationBean Type Parameters** 🟢 MINOR
**File**: `config/ServletFilterConfig.java`

**Challenge**: FilterRegistrationBean<Filter> type parameter changes from javax to jakarta.

**Manual Steps Required**:
1. Update Filter type parameter
2. Verify filter registration still works
3. Test filter ordering and precedence

## 🎯 Why This Project is Ideal for Testing Migration Tools

1. **Multi-Layer Complexity**: Security, servlet, reflection, and JAXB challenges
2. **Hidden Dependencies**: Reflection and dynamic class loading
3. **Architectural Decisions**: ACL replacement requires design choices
4. **No Simple Find/Replace**: Behavioral differences need testing
5. **Real-World Patterns**: Mirrors actual legacy enterprise applications

## 📋 Migration Checklist for Java 17 + Spring Boot 3.x

Manual intervention required at each step:

- [ ] **Phase 1: Analysis**
  - [ ] Inventory all java.security.acl usage
  - [ ] Identify all reflective API calls
  - [ ] List all javax.* dependencies
  - [ ] Review custom JAXB adapters

- [ ] **Phase 2: Architectural Decisions**
  - [ ] Choose ACL replacement strategy
  - [ ] Design new security permission model
  - [ ] Plan reflection removal strategy

- [ ] **Phase 3: Code Migration**
  - [ ] Remove/replace java.security.acl
  - [ ] Replace reflective deprecated API calls
  - [ ] Migrate javax.* to jakarta.*
  - [ ] Update JAXB adapters and marshallers
  - [ ] Update servlet filters

- [ ] **Phase 4: Testing & Validation**
  - [ ] Unit test all migrated components
  - [ ] Integration test security layer
  - [ ] Performance test (especially reflection replacements)
  - [ ] Validate XML marshalling/unmarshalling
  - [ ] Test filter chains and session management

- [ ] **Phase 5: Verification**
  - [ ] Build with Java 17
  - [ ] Run all tests
  - [ ] Verify behavioral equivalence
  - [ ] Performance benchmarking
  - [ ] Security audit

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
