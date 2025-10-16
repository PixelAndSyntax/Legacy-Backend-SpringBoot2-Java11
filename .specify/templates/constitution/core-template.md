# Constitution Core Standards

<!--
Section: core
Priority: critical
Applies to: all projects
Version: 1.0.0
Last Updated: [YYYY-MM-DD]
Project: [PROJECT_NAME]
-->

## 1. Technology Stack Standards

| Component              | Requirement                         | Priority | Notes                     |
| ---------------------- | ----------------------------------- | -------- | ------------------------- |
| **Runtime**            | [RUNTIME_TECHNOLOGY]                | MUST     | [RUNTIME_VERSION_REQ]     |
| Runtime Security       | [RUNTIME_SECURITY_REQUIREMENT]      | MUST     | Mandatory compliance      |
| Runtime Optimization   | [RUNTIME_OPTIMIZATION_GUIDELINE]    | SHOULD   | Performance best practice |
| Runtime Monitoring     | [RUNTIME_MONITORING_GUIDELINE]      | SHOULD   | Observability             |
| Runtime Enhancement    | [RUNTIME_ENHANCEMENT_OPTION]        | COULD    | Optional feature          |
| **Language**           | [LANGUAGE] [VERSION_REQUIREMENT]    | MUST     | Primary language          |
| Language Strictness    | [LANGUAGE_STRICTNESS_REQUIREMENT]   | MUST     | Type safety/strict mode   |
| Language Linting       | [LANGUAGE_LINTING_REQUIREMENT]      | MUST     | Code quality              |
| Language Best Practice | [LANGUAGE_BEST_PRACTICE]            | SHOULD   | Recommended patterns      |
| Language Documentation | [LANGUAGE_DOCUMENTATION_GUIDELINE]  | SHOULD   | Code documentation        |
| Language Optional      | [LANGUAGE_OPTIONAL_FEATURE]         | COULD    | Advanced features         |
| **Compute Platform**   | [COMPUTE_PLATFORM]                  | MUST     | All deployments           |
| Compute Security       | [COMPUTE_SECURITY_REQUIREMENT]      | MUST     | Security compliance       |
| Compute Config         | [COMPUTE_CONFIGURATION_REQUIREMENT] | MUST     | Standard configuration    |
| Compute Optimization   | [COMPUTE_OPTIMIZATION_GUIDELINE]    | SHOULD   | Performance tuning        |
| Compute Monitoring     | [COMPUTE_MONITORING_GUIDELINE]      | SHOULD   | Health checks             |
| **Database**           | [DATABASE_TECHNOLOGY]               | MUST     | [DATABASE_USE_CASE]       |
| Database Backup        | [DATABASE_BACKUP_REQUIREMENT]       | MUST     | Data protection           |
| Database Security      | [DATABASE_SECURITY_REQUIREMENT]     | MUST     | Access control            |
| Database Optimization  | [DATABASE_OPTIMIZATION_GUIDELINE]   | SHOULD   | Query performance         |
| Database Monitoring    | [DATABASE_MONITORING_GUIDELINE]     | SHOULD   | Health metrics            |
| Database Optional      | [DATABASE_OPTIONAL_FEATURE]         | COULD    | Advanced features         |

### Technology Prohibitions (WON'T without RFC)

- Alternative runtimes without formal RFC approval
- [PROHIBITED_RUNTIME_PRACTICE]
- [PROHIBITED_LANGUAGE_PRACTICE]
- [DEPRECATED_LANGUAGE_FEATURE]
- Alternative compute platforms
- [PROHIBITED_COMPUTE_PRACTICE]
- [PROHIBITED_DATABASE_PRACTICE]
- Alternative databases without RFC approval

---

## 2. Coding Standards

| Area               | Standard                             | Enforcement | Validation          |
| ------------------ | ------------------------------------ | ----------- | ------------------- |
| **Language**       | [LANGUAGE_STANDARD]                  | MUST        | Automated linting   |
| **Type Safety**    | [TYPE_SAFETY_REQUIREMENTS]           | MUST        | Compile-time        |
| **Async Patterns** | [ASYNC_PATTERN_REQUIREMENTS]         | MUST        | Code review         |
| **Modularity**     | [MODULARITY_STANDARDS]               | MUST        | Architecture review |
| **Error Handling** | [ERROR_HANDLING_PATTERN]             | MUST        | Automated linting   |
| **Logging**        | [LOGGING_LIBRARY]; [LOGGING_PATTERN] | MUST        | Automated scanning  |
| **Secrets**        | [SECRET_HANDLING_RULES]              | MUST        | Secret scanning     |
| **Validation**     | [INPUT_VALIDATION_REQUIREMENTS]      | MUST        | Security review     |
| **DTOs/Models**    | [DTO_NAMING_CONVENTIONS]             | MUST        | Code review         |

### Error Handling Example

```typescript
try {
  const result = await operation();
  return result;
} catch (error) {
  logger.error("Operation failed", { correlationId, error });
  throw error;
}
```

### Core Requirements

- **Logging**: Structured format, correlation ID, no secrets, consistent field names
- **Secrets**: No plaintext in code/env/logs/errors
- **Validation**: Validate/sanitize all external inputs, type & boundary checking
- **DTOs**: Clear naming, type annotations, immutability preferred, validation methods

---

## 3. API Versioning Standards

| Versioning Aspect | Requirement                                | Priority | Notes                    |
| ----------------- | ------------------------------------------ | -------- | ------------------------ |
| **Strategy**      | [API_VERSIONING_STRATEGY]                  | MUST     | URL path or header based |
| Version support   | Support previous version during transition | MUST     | Minimum 6 months         |
| Breaking changes  | Document all breaking changes              | MUST     | In CHANGELOG and docs    |
| Deprecation       | Deprecate endpoints before removal         | MUST     | Minimum 3 months notice  |
| Migration guides  | Provide guides for major versions          | SHOULD   | With code examples       |

---

## 4. Enforcement and Validation

| Standard Area      | Enforcement Level | Validation Method     | Automated | Frequency     |
| ------------------ | ----------------- | --------------------- | --------- | ------------- |
| Language Standards | Mandatory         | Linting + Compilation | Yes       | Every commit  |
| Type Safety        | Mandatory         | Compile-time          | Yes       | Every commit  |
| Error Handling     | Mandatory         | Linting + Review      | Partial   | Every commit  |
| Logging            | Mandatory         | Automated scanning    | Yes       | Every commit  |
| Secrets            | Mandatory         | Secret scanning       | Yes       | Every commit  |
| Input Validation   | Mandatory         | Security review       | Partial   | Per PR        |
| Versioning         | Mandatory         | Automated checks      | Yes       | Every release |
| Architecture       | Mandatory         | Architecture review   | No        | Per feature   |
