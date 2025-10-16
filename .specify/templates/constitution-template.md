# Development Constitution

---

<!-- Section: architecture -->

## Architectural Patterns

### CRUD Pattern

**Routes**:

- POST: Create new resource
- GET: Retrieve resource(s)
- PATCH: Update existing resource
- DELETE: Remove resource

**Benefits**: Consistent API design, predictable behavior, clear separation of concerns, standard HTTP semantics, easy testing

### Security Requirements

**Defense in Depth**: Multiple security layers with input validation, authentication, authorization, and output sanitization
**Zero Trust**: Verify all requests, validate all inputs, authenticate all users, authorize all actions
**Monitoring**: Log all security events, detect anomalies, track access patterns, monitor for threats

---

<!-- Section: core -->

## Coding Standards

| Area           | Standard                             | Enforcement | Validation          |
| -------------- | ------------------------------------ | ----------- | ------------------- |
| Language       | [LANGUAGE_STANDARD]                  | Mandatory   | Automated linting   |
| Type Safety    | [TYPE_SAFETY_REQUIREMENTS]           | Mandatory   | Compile-time        |
| Async Patterns | [ASYNC_PATTERN_REQUIREMENTS]         | Mandatory   | Code review         |
| Modularity     | [MODULARITY_STANDARDS]               | Mandatory   | Architecture review |
| Error Handling | [ERROR_HANDLING_PATTERN]             | Mandatory   | Automated linting   |
| Logging        | [LOGGING_LIBRARY]; [LOGGING_PATTERN] | Mandatory   | Automated scanning  |
| Secrets        | [SECRET_HANDLING_RULES]              | Mandatory   | Secret scanning     |
| Validation     | [INPUT_VALIDATION_REQUIREMENTS]      | Mandatory   | Security review     |
| DTOs/Models    | [DTO_NAMING_CONVENTIONS]             | Mandatory   | Code review         |

### Error Handling Example

```
try {
  const result = await operation();
  return result;
} catch (error) {
  logger.error('Operation failed', { correlationId, error });
  throw error;
}
```

### Core Requirements

- **Logging**: Structured format, correlation ID, no secrets, consistent field names
- **Secrets**: No plaintext in code/env/logs/errors
- **Validation**: Validate/sanitize all external inputs, type & boundary checking
- **DTOs**: Clear naming, type annotations, immutability preferred, validation methods

---

<!-- Section: testing -->

## Testing Standards

### Coverage

- **Requirements**: [COVERAGE_REQUIREMENTS] (Mandatory, validated via automated CI)
- **Threshold**: [COVERAGE_THRESHOLD_POLICY]
- **Exclusions**: [COVERAGE_EXCLUSIONS]

### Test Organization

| Type          | Location                  | Directory                      | Suffix               | Constraints                                                                                                   |
| ------------- | ------------------------- | ------------------------------ | -------------------- | ------------------------------------------------------------------------------------------------------------- |
| [TEST_TYPE_1] | [TEST_LOCATION_1]         | [TEST_DIRECTORY_1]             | [TEST_SUFFIX_1]      | Must: Colocate with source, follow naming. Must Not: Place in [PROHIBITED_TEST_LOCATION_1]                    |
| [TEST_TYPE_2] | [TEST_LOCATION_2]         | [TEST_DIRECTORY_2]             | [CONTRACT_SUFFIX]    | [TEST_TYPE_2_DESCRIPTION]                                                                                     |
| [TEST_TYPE_3] | Exclusive directory       | [INTEGRATION_DIRECTORY]        | [INTEGRATION_SUFFIX] | Must: Dedicated dir, test real integrations, setup/teardown. Must Not: Mix with unit tests, excessive mocking |
| [TEST_TYPE_4] | [PERFORMANCE_TEST_POLICY] | [PERFORMANCE_DIRECTORY_STATUS] | -                    | [PERFORMANCE_OPTIMIZATION_APPROACH], [PERFORMANCE_TEST_EXCEPTION_POLICY]                                      |

**Examples**: Source: [SOURCE_EXAMPLE_1] → Test: [TEST_EXAMPLE_1] | Source: [SOURCE_EXAMPLE_2] → Test: [TEST_EXAMPLE_2]

**Security Testing**: [SECURITY_TEST_TYPES] | Location: [SECURITY_TEST_LOCATION], Suffix: [SECURITY_TEST_SUFFIX]

| Category         | Requirements                         | Examples                                   |
| ---------------- | ------------------------------------ | ------------------------------------------ |
| Authentication   | [AUTHENTICATION_TEST_REQUIREMENTS]   | Bypass, invalid tokens, session validation |
| Authorization    | [AUTHORIZATION_TEST_REQUIREMENTS]    | Privilege escalation, role boundaries      |
| Input Validation | [INPUT_VALIDATION_TEST_REQUIREMENTS] | SQL injection, XSS, command injection      |
| Cryptographic    | [CRYPTOGRAPHIC_TEST_REQUIREMENTS]    | Encryption, key management, secure random  |

**Integration**: [INTEGRATION_TEST_REQUIREMENTS] | Logging: [INTEGRATION_TEST_LOGGING_REQUIREMENTS] | Mocking: [INTEGRATION_TEST_MOCKING_SCOPE] | Must: Real service interactions, logging, error scenarios | Must Not: Mock critical integrations, skip cleanup

**Mocking**: Unit: [UNIT_TEST_MOCKING_PERMISSIONS] | Implementation: [MOCK_IMPLEMENTATION_REQUIREMENTS] | Practices: Consistent framework, mock external deps, verify interactions, realistic behavior

---

<!-- Section: observability -->

## Logging Standards

**Entry**: [LOGGING_ENTRY_PATTERN] | [LOGGING_MESSAGE_GUIDELINES] | [LOGGING_PARAMETER_INCLUSION] | [LOGGING_FORMAT_PATTERN] (Mandatory)  
**Must**: Log at entry, include correlationId, structured format, concise | **Must Not**: Log sensitive data, string concatenation, exceed size

**Test Env**: [TEST_LOGGING_OUTPUT_REQUIREMENTS] | [TEST_LOGGING_FORMAT_CONSISTENCY] | [TEST_LOGGING_CLOUD_PREVENTION]  
**Must**: Maintain production format, console output, parseable | **Must Not**: Cloud services, skip critical fields

### Required Fields

| Field         | Type   | Description                              | Validation            | When                   |
| ------------- | ------ | ---------------------------------------- | --------------------- | ---------------------- |
| correlationId | string | Unique request trace ID                  | UUID or similar       | Always                 |
| timestamp     | string | ISO 8601 timestamp                       | Valid ISO8601         | Always                 |
| level         | enum   | Log level (INFO/DEBUG/WARN/ERROR)        | One of allowed values | Always                 |
| service       | string | Service/component name                   | Non-empty string      | Always                 |
| message       | string | Human-readable log message               | Non-empty descriptive | Always                 |
| operation     | string | Function/method being executed           | -                     | When available         |
| userId        | string | User identifier                          | -                     | User context exists    |
| sessionId     | string | Session identifier                       | -                     | Session context exists |
| requestId     | string | HTTP/API request ID                      | -                     | HTTP request context   |
| duration      | number | Operation time (ms)                      | -                     | Operation complete     |
| outcome       | enum   | Result (success/failure/timeout/partial) | -                     | Operation complete     |
| errorCode     | string | Application-specific error code          | -                     | level == ERROR         |
| errorMessage  | string | Error description                        | -                     | level == ERROR         |
| errorType     | string | Error category/classification            | -                     | level == ERROR         |
| stackTrace    | string | Technical stack trace (dev only)         | -                     | level == ERROR         |

### Implementation Examples

```
// Entry: LOG.INFO("[OPERATION_NAME] started", {correlationId, userId, requestId})
// Success: LOG.INFO("[OPERATION_NAME] completed", {correlationId, outcome: "success", duration})
// Error: LOG.ERROR("[OPERATION_NAME] failed", {correlationId, errorCode, errorType})

FUNCTION serviceOperation(request) {
  correlationId = request.correlationId OR generateId()
  LOG.INFO("Operation started", {correlationId, operation, userId, requestId})
  TRY {
    result = processRequest(request)
    LOG.INFO("Operation completed", {correlationId, outcome: "success", duration})
    RETURN result
  } CATCH (error) {
    LOG.ERROR("Operation failed", {correlationId, errorCode, errorType})
    THROW error
  }
}
```

**Requirements**: Always include correlationId | Never log secrets (automated scanning) | Structured logging (linting) | Consistent naming (schema validation)

---

<!-- Section: architecture -->

## Database Guidelines

| Aspect   | Rule                                              | Must                                                  | Must Not                                |
| -------- | ------------------------------------------------- | ----------------------------------------------------- | --------------------------------------- |
| Keys     | [KEY_DEFINITION_REQUIREMENTS] (schema validation) | Clear partition/sort keys, naming conventions         | Ambiguous patterns, hot partitions      |
| TTL      | [TTL_REQUIREMENTS] (code review)                  | Set for transient data, document rationale, lifecycle | Without justification, skip cleanup     |
| GSIs     | [GSI_ADDITION_CRITERIA] (architecture review)     | Justify necessity, project required attrs, monitor    | Unnecessary GSIs, duplicate primary key |
| Capacity | [CAPACITY_CONFIGURATION] (operations review)      | Justify mode, monitor usage, optimize cost            | Over-provision, ignore costs            |

**GSIs When**: Required: Alt query pattern, performance justified, positive cost-benefit | Prohibited: Table scan sufficient, uncommon query, maintenance > benefit  
**Capacity Modes**: On-Demand: Unpredictable traffic, no planning, auto-scale, higher cost | Provisioned: Predictable traffic, lower cost at scale, requires planning

---

<!-- Section: architecture -->

## API Standards

| Category       | Standards                                                                                                             |
| -------------- | --------------------------------------------------------------------------------------------------------------------- |
| Security       | [API_SECURITY_HEADERS_REQUIREMENT], [ERROR_RESPONSE_STANDARDS], [ERROR_LOCALIZATION_POLICY]                           |
| Protection     | [CORS_CONFIGURATION_REQUIREMENTS], [CSRF_PROTECTION_MECHANISM], [REQUEST_SIZE_LIMITS], [RATE_LIMITING_IMPLEMENTATION] |
| Authentication | [API_AUTHENTICATION_REQUIREMENTS], [TOKEN_VALIDATION_STANDARDS], [SCOPE_BASED_ACCESS_CONTROL]                         |
| Validation     | [INPUT_PARAMETER_VALIDATION], [OUTPUT_DATA_SANITIZATION], [CONTENT_TYPE_VALIDATION]                                   |
| Versioning     | [API_VERSIONING_STRATEGY], [BACKWARD_COMPATIBILITY_POLICY], [SECURITY_PATCH_DEPLOYMENT]                               |
| Resilience     | [API_THROTTLING_REQUIREMENTS], [DDoS_PROTECTION_MEASURES], [GEOGRAPHIC_ACCESS_CONTROLS]                               |

---

<!-- Section: security -->

## Security Standards

| Category             | Standards                                                                                           |
| -------------------- | --------------------------------------------------------------------------------------------------- |
| Access Control       | [ACCESS_CONTROL_PRINCIPLE_REQUIREMENT], [SECRET_STORAGE_POLICY], [SECURITY_SAFEGUARDS_REQUIREMENTS] |
| Auth & Authorization | [AUTH_MECHANISM_REQUIREMENTS], [SESSION_MANAGEMENT_POLICY], [ROLE_BASED_ACCESS_CONTROL]             |
| Data Protection      | [DATA_ENCRYPTION_REQUIREMENTS], [PII_HANDLING_POLICY], [DATA_CLASSIFICATION_STANDARDS]              |
| Input Security       | [INPUT_VALIDATION_SECURITY], [SQL_INJECTION_PREVENTION], [XSS_PREVENTION_MEASURES]                  |
| Monitoring           | [SECURITY_LOGGING_REQUIREMENTS], [LOG_RETENTION_POLICY], [ANOMALY_DETECTION_REQUIREMENTS]           |
| Secrets Management   | [SECRET_HANDLING_RULES], [SECRET_ROTATION_POLICY], [ENVIRONMENT_SEPARATION]                         |
| Network Security     | [NETWORK_SEGMENTATION_REQUIREMENTS], [FIREWALL_RULES_POLICY], [TLS_CONFIGURATION_STANDARDS]         |
| Vulnerability Mgmt   | [DEPENDENCY_SCANNING_REQUIREMENTS], [SECURITY_UPDATE_POLICY], [PENETRATION_TESTING_SCHEDULE]        |
| Incident Response    | [SECURITY_INCIDENT_PROCEDURES], [BREACH_NOTIFICATION_REQUIREMENTS], [FORENSIC_CAPABILITIES]         |
| Compliance           | [REGULATORY_COMPLIANCE_REQUIREMENTS], [SECURITY_REVIEW_PROCESS], [THIRD_PARTY_SECURITY_ASSESSMENT]  |
| Exceptions           | [DEVIATION_APPROVAL_PROCESS], [CONSTITUTION_UPDATE_REQUIREMENTS]                                    |
