# Constitution Testing Standards

<!--
Section: testing
Priority: critical
Applies to: all projects
Dependencies: [core]
Version: 1.0.0
Last Updated: [YYYY-MM-DD]
Project: [PROJECT_NAME]
-->

## 1. Test Coverage Standards

| Coverage Type | Requirement              | Threshold             | Enforcement        |
| ------------- | ------------------------ | --------------------- | ------------------ |
| **Overall**   | [COVERAGE_REQUIREMENTS]  | [COVERAGE_PERCENT]    | Automated CI check |
| Statement     | [STATEMENT_COVERAGE_REQ] | [STATEMENT_THRESHOLD] | CI blocking        |
| Branch        | [BRANCH_COVERAGE_REQ]    | [BRANCH_THRESHOLD]    | CI blocking        |
| Function      | [FUNCTION_COVERAGE_REQ]  | [FUNCTION_THRESHOLD]  | CI blocking        |
| Exclusions    | [COVERAGE_EXCLUSIONS]    | N/A                   | Code review        |

---

## 2. Test Organization Standards

| Test Type             | Location                 | Suffix               | Colocated | Priority |
| --------------------- | ------------------------ | -------------------- | --------- | -------- |
| **Unit Tests**        | [UNIT_TEST_LOCATION]     | [UNIT_TEST_SUFFIX]   | Yes       | MUST     |
| **Contract Tests**    | [CONTRACT_TEST_LOCATION] | [CONTRACT_SUFFIX]    | Yes       | MUST     |
| **Integration Tests** | [INTEGRATION_DIRECTORY]  | [INTEGRATION_SUFFIX] | No        | MUST     |
| **Security Tests**    | [SECURITY_TEST_LOCATION] | [SECURITY_SUFFIX]    | Yes       | MUST     |
| **Flow Tests**        | [FLOW_TEST_DIRECTORY]    | [FLOW_SUFFIX]        | No        | SHOULD   |
| **Performance Tests** | [PERFORMANCE_DIRECTORY]  | [PERF_SUFFIX]        | No        | COULD    |

### File Organization Examples

| Source File             | Test File                                   | Location Rule           |
| ----------------------- | ------------------------------------------- | ----------------------- |
| `src/service/user.ts`   | `src/service/user.test.ts`                  | Colocated unit test     |
| `src/service/user.ts`   | `src/service/user.contract.ts`              | Colocated contract test |
| `src/handlers/api.ts`   | `tests/integration/api.integration.test.ts` | Separate integration    |
| `src/auth/validator.ts` | `src/auth/validator.security.test.ts`       | Colocated security      |

---

## 3. Test Type Requirements

### Unit Tests

| Requirement           | Description                   | Priority | Validation       |
| --------------------- | ----------------------------- | -------- | ---------------- |
| Colocation            | Must be next to source file   | MUST     | File structure   |
| Naming Convention     | [UNIT_TEST_NAMING]            | MUST     | Automated lint   |
| Mocking Allowed       | [UNIT_TEST_MOCKING_POLICY]    | MUST     | Code review      |
| External Dependencies | Must mock all external calls  | MUST     | Test review      |
| Fast Execution        | [UNIT_TEST_SPEED_REQUIREMENT] | SHOULD   | Performance test |

### Integration Tests

| Requirement     | Description                         | Priority | Validation     |
| --------------- | ----------------------------------- | -------- | -------------- |
| Location        | Dedicated `tests/integration/` dir  | MUST     | File structure |
| Real Services   | [INTEGRATION_TEST_MOCKING_SCOPE]    | MUST     | Code review    |
| Setup/Teardown  | Include proper lifecycle management | MUST     | Test review    |
| Logging         | [INTEGRATION_TEST_LOGGING_REQ]      | MUST     | Code review    |
| Error Scenarios | Test failure paths                  | MUST     | Test coverage  |

### Contract Tests

| Requirement            | Description                       | Priority | Validation          |
| ---------------------- | --------------------------------- | -------- | ------------------- |
| API Contracts          | [CONTRACT_TEST_REQUIREMENTS]      | MUST     | API review          |
| Schema Validation      | Validate request/response schemas | MUST     | Automated           |
| Backward Compatibility | Test API version compatibility    | MUST     | CI check            |
| Consumer-Driven        | [CONSUMER_DRIVEN_CONTRACT_POLICY] | SHOULD   | Architecture review |

---

## 4. Security Testing Standards

| Test Category        | Requirement                      | Priority | Examples                              |
| -------------------- | -------------------------------- | -------- | ------------------------------------- |
| **Authentication**   | [AUTH_TEST_REQUIREMENTS]         | MUST     | Invalid tokens, bypass attempts       |
| Token Validation     | Test all token scenarios         | MUST     | Expired, malformed, missing           |
| Session Management   | [SESSION_TEST_REQUIREMENTS]      | MUST     | Timeout, hijacking                    |
| **Authorization**    | [AUTHZ_TEST_REQUIREMENTS]        | MUST     | Role boundaries, escalation           |
| Permission Checks    | Test all permission combinations | MUST     | Access control validation             |
| **Input Validation** | [INPUT_VALIDATION_TEST_REQ]      | MUST     | SQL injection, XSS, command injection |
| Sanitization         | Test output encoding             | MUST     | XSS prevention                        |
| **Cryptography**     | [CRYPTO_TEST_REQUIREMENTS]       | MUST     | Encryption, key management            |
| Key Rotation         | Test key lifecycle               | SHOULD   | Rotation procedures                   |

### Penetration Testing

| Test Type          | Requirement                       | Frequency     | Priority |
| ------------------ | --------------------------------- | ------------- | -------- |
| Automated Scans    | [AUTOMATED_PENTEST_REQ]           | Every deploy  | MUST     |
| Manual Testing     | [MANUAL_PENTEST_REQUIREMENTS]     | Quarterly     | SHOULD   |
| Vulnerability Scan | [VULNERABILITY_SCAN_REQUIREMENTS] | Weekly        | MUST     |
| Security Baseline  | [SECURITY_BASELINE_VALIDATION]    | Every release | MUST     |

---

## 5. Mocking Standards

| Context               | Mocking Policy                  | Priority | Rationale               |
| --------------------- | ------------------------------- | -------- | ----------------------- |
| **Unit Tests**        | [UNIT_TEST_MOCKING_PERMISSIONS] | MUST     | Isolate unit under test |
| External Services     | Always mock                     | MUST     | Fast, deterministic     |
| Database Calls        | Always mock                     | MUST     | No real DB connections  |
| **Integration Tests** | [INTEGRATION_MOCKING_SCOPE]     | MUST     | Test real interactions  |
| Critical Services     | Do not mock                     | MUST     | Validate integration    |
| Third-party APIs      | [THIRD_PARTY_MOCKING_POLICY]    | SHOULD   | Use test environments   |

### Mocking Best Practices

| Practice             | Requirement                  | Priority |
| -------------------- | ---------------------------- | -------- |
| Consistent Framework | [MOCKING_FRAMEWORK]          | MUST     |
| Realistic Behavior   | Mock responses match reality | MUST     |
| Verify Interactions  | Assert mock calls            | SHOULD   |
| Clear Mock Setup     | Document mock expectations   | SHOULD   |

---

## 6. Test Execution Requirements

| Requirement        | Description                         | Priority | Enforcement      |
| ------------------ | ----------------------------------- | -------- | ---------------- |
| Pre-commit Tests   | Run unit tests before commit        | MUST     | Git hooks        |
| CI Pipeline Tests  | Run all tests on PR                 | MUST     | CI configuration |
| Coverage Threshold | Block merge if below threshold      | MUST     | CI gates         |
| Test Isolation     | Tests must not depend on each other | MUST     | Test framework   |
| Parallel Execution | [PARALLEL_TEST_POLICY]              | SHOULD   | Performance      |
