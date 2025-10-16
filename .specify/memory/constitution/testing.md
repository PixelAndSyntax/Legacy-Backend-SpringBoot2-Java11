# Constitution Testing Standards

<!--
SYNC IMPACT REPORT (2025-10-16):
- File: testing.md
- Version: 1.0.0 → 1.0.0 (initial creation from template)
- Modified Principles: All (initial population)
- Added Sections: Complete testing standards for Spring Boot project
- Removed Sections: None
- Templates Requiring Updates: None (initial setup)
- Follow-up TODOs: None
- Impact: Establishes baseline testing standards
-->

<!--
Section: testing
Priority: critical
Applies to: all projects
Dependencies: [core]
Version: 1.0.0
Last Updated: 2025-10-16
Project: Legacy-Backend-SpringBoot2-Java11
-->

## 1. Test Coverage Standards

| Coverage Type | Requirement                      | Threshold | Enforcement        |
| ------------- | -------------------------------- | --------- | ------------------ |
| **Overall**   | Line coverage for service layer  | 80%       | Manual review      |
| Statement     | Statement coverage encouraged    | 70%       | Best effort        |
| Branch        | Branch coverage encouraged       | 60%       | Best effort        |
| Function      | Public methods must have tests   | 100%      | Code review        |
| Exclusions    | Legacy package excluded from coverage | N/A       | Intentional exclusion |

**Note**: This is a legacy demonstration project focused on migration complexity rather than production-grade coverage. Coverage thresholds are guidelines, not CI blockers.

---

## 2. Test Organization Standards

| Test Type             | Location                           | Suffix                | Colocated | Priority |
| --------------------- | ---------------------------------- | --------------------- | --------- | -------- |
| **Unit Tests**        | src/test/java (mirror src/main/java) | Test.java             | No        | MUST     |
| **Integration Tests** | src/test/java                      | Test.java             | No        | MUST     |
| **MockMvc Tests**     | src/test/java (controller package) | Test.java             | No        | MUST     |
| **JAXB Tests**        | src/test/java                      | Test.java             | No        | MUST     |

### File Organization Examples

| Source File                            | Test File                                      | Location Rule                 |
| -------------------------------------- | ---------------------------------------------- | ----------------------------- |
| `src/main/java/.../AsyncService.java`  | `src/test/java/.../AsyncServiceTest.java`      | Mirror package structure      |
| `src/main/java/.../LegacyController.java` | `src/test/java/.../LegacyControllerTest.java`  | Mirror package structure      |
| `src/main/java/.../XmlCustomer.java`   | `src/test/java/.../JaxbTest.java`              | JAXB marshalling test         |

**Current Test Files**:
- `AsyncServiceTest.java` - Tests async service patterns
- `LegacyControllerTest.java` - MockMvc REST endpoint tests
- `JaxbTest.java` - JAXB marshalling/unmarshalling tests

---

## 3. Test Type Requirements

### Unit Tests

| Requirement           | Description                             | Priority | Validation       |
| --------------------- | --------------------------------------- | -------- | ---------------- |
| Naming Convention     | Class name + "Test" suffix              | MUST     | Maven Surefire   |
| Mocking Allowed       | Use Mockito for service dependencies    | MUST     | Test framework   |
| External Dependencies | Mock all external calls (DB, HTTP)      | MUST     | Test isolation   |
| Fast Execution        | Unit tests should run in <5 seconds total | SHOULD   | Performance test |
| Assertions            | Use JUnit 5 assertions or AssertJ       | MUST     | Test framework   |

**Example**: `AsyncServiceTest` uses `@Mock` and `@InjectMocks` to test AsyncService in isolation.

### Integration Tests (MockMvc)

| Requirement     | Description                                    | Priority | Validation     |
| --------------- | ---------------------------------------------- | -------- | -------------- |
| Spring Context  | Use @SpringBootTest or @WebMvcTest             | MUST     | Test framework |
| Real Services   | Load actual Spring beans (not mocked unless necessary) | MUST     | Integration testing |
| MockMvc         | Use MockMvc for HTTP endpoint testing          | MUST     | Spring Test    |
| Database        | Use H2 in-memory with schema.sql and data.sql  | MUST     | Test isolation |
| Transaction Rollback | Use @Transactional to rollback test data      | SHOULD   | Data cleanup   |

**Example**: `LegacyControllerTest` uses `@WebMvcTest` with MockMvc to test REST endpoints.

### JAXB Tests

| Requirement            | Description                              | Priority | Validation          |
| ---------------------- | ---------------------------------------- | -------- | ------------------- |
| Marshalling Test       | Test object to XML conversion            | MUST     | JAXB validation     |
| Unmarshalling Test     | Test XML to object conversion            | MUST     | JAXB validation     |
| Schema Validation      | Validate XML against expected structure  | SHOULD   | XML assertions      |
| Namespace Handling     | Test XML namespace correctness           | SHOULD   | JAXB configuration  |

**Example**: `JaxbTest` validates XmlCustomer marshalling with javax.xml.bind (migration to jakarta.xml.bind required).

---

## 4. Security Testing Standards

| Test Category        | Requirement                              | Priority | Examples                          |
| -------------------- | ---------------------------------------- | -------- | --------------------------------- |
| **Input Validation** | Test invalid inputs are rejected         | MUST     | Null, empty, malformed inputs     |
| Boundary Testing     | Test edge cases (min, max, boundary values) | SHOULD   | String length, numeric bounds     |
| **Error Handling**   | Test error responses are appropriate     | MUST     | 400 Bad Request, 404 Not Found    |
| Exception Handling   | Test exceptions are logged and handled   | MUST     | Service layer exception tests     |

**Note**: Full security testing (authentication, authorization, penetration testing) is not implemented in this baseline demo project. The CustomAclManager security layer requires manual testing after migration due to java.security.acl removal in Java 17.

---

## 5. Mocking Standards

| Context               | Mocking Policy                       | Priority | Rationale                      |
| --------------------- | ------------------------------------ | -------- | ------------------------------ |
| **Unit Tests**        | Mock all external dependencies       | MUST     | Isolate unit under test        |
| Service Dependencies  | Mock repository and other services   | MUST     | Fast, deterministic tests      |
| Database Calls        | Mock repository methods              | MUST     | No real DB connections in unit |
| **Integration Tests** | Minimize mocking (real Spring beans) | MUST     | Test real interactions         |
| MockMvc Tests         | Use real controllers and services    | MUST     | Validate integration           |

### Mocking Framework

| Practice             | Requirement              | Priority |
| -------------------- | ------------------------ | -------- |
| Framework            | Mockito (Spring default) | MUST     |
| Mock Annotations     | Use @Mock and @InjectMocks | SHOULD   |
| Realistic Behavior   | Mock responses match reality | MUST     |
| Verify Interactions  | Use verify() for critical calls | SHOULD   |

**Example from AsyncServiceTest**:
```java
@Mock
private SomeRepository repository;

@InjectMocks
private AsyncService asyncService;
```

---

## 6. Test Execution Requirements

| Requirement        | Description                             | Priority | Enforcement        |
| ------------------ | --------------------------------------- | -------- | ------------------ |
| Maven Test Phase   | Run all tests with `mvn test`           | MUST     | Maven Surefire     |
| Pre-commit Tests   | Run tests before committing             | SHOULD   | Developer discipline |
| CI Pipeline Tests  | All tests must pass in CI               | MUST     | CI configuration   |
| Test Isolation     | Tests must not depend on each other     | MUST     | JUnit execution    |
| Parallel Execution | Not configured (sequential execution)   | N/A      | Default behavior   |

### Test Execution Commands

```bash
# Run all tests
mvn -s .mvn/settings.xml test

# Run specific test class
mvn -s .mvn/settings.xml test -Dtest=AsyncServiceTest

# Run tests with coverage (if configured)
mvn -s .mvn/settings.xml test jacoco:report
```

---

## 7. Test Data Management

| Requirement           | Description                           | Priority | Implementation       |
| --------------------- | ------------------------------------- | -------- | -------------------- |
| **Schema Setup**      | schema.sql defines table structure    | MUST     | H2 initialization    |
| **Data Seeding**      | data.sql provides test data           | MUST     | H2 initialization    |
| Test Data Isolation   | Each test should be independent       | MUST     | @Transactional rollback |
| Fixture Management    | Use @BeforeEach for test setup        | SHOULD   | JUnit lifecycle      |
| **Cleanup**           | Tests should not leave side effects   | MUST     | Transaction rollback |

**Current Setup**:
- `src/main/resources/schema.sql` - CUSTOMER table definition
- `src/main/resources/data.sql` - Sample customer records

---

## 8. Legacy Code Testing Considerations

### Migration Testing Requirements

| Legacy Component               | Test Requirement                              | Priority | Migration Impact                 |
| ------------------------------ | --------------------------------------------- | -------- | -------------------------------- |
| **javax.xml.bind (JAXB)**      | Test marshalling still works after jakarta migration | MUST     | JaxbTest must pass               |
| **@Async patterns**            | Test that invalid patterns fail in Spring 6   | MUST     | AsyncServiceTest needs updates   |
| **javax.servlet Filter**       | Test filter behavior after jakarta migration  | MUST     | New filter integration tests     |
| **java.security.acl**          | Manual testing after ACL replacement          | MUST     | Complete security layer retest   |
| **Thread APIs**                | Test cooperative cancellation replacement     | MUST     | New thread service tests         |

### Test Maintenance During Migration

| Phase                  | Testing Focus                                  | Priority |
| ---------------------- | ---------------------------------------------- | -------- |
| Pre-migration Baseline | Run all tests, document passing state (5/5)    | MUST     |
| Dependency Migration   | Test after each dependency update              | MUST     |
| API Replacement        | Test each deprecated API replacement           | MUST     |
| Post-migration         | Full regression suite with Java 17 + Boot 3.x  | MUST     |

**Current Baseline**: All 5 tests passing ✅
- AsyncServiceTest: 1 test
- LegacyControllerTest: 3 tests  
- JaxbTest: 1 test

---

## 9. Test Reporting

| Requirement           | Description                           | Priority | Tool             |
| --------------------- | ------------------------------------- | -------- | ---------------- |
| **Console Output**    | Display test results in console       | MUST     | Maven Surefire   |
| **Test Summary**      | Show passed/failed/skipped counts     | MUST     | Maven output     |
| Failure Details       | Show stack traces for failures        | MUST     | Surefire reports |
| **XML Reports**       | Generate XML reports in target/       | MUST     | Surefire XML     |
| Coverage Reports      | Optional JaCoCo HTML reports          | COULD    | JaCoCo plugin    |

### Expected Output Format

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.example.legacydemo.AsyncServiceTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

Running com.example.legacydemo.LegacyControllerTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0

Running com.example.legacydemo.JaxbTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

Results :

Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```
