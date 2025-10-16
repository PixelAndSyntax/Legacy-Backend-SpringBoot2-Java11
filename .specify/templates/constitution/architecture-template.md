# Constitution Architecture Standards

<!--
Section: architecture
Priority: high
Applies to: all projects
Dependencies: [core]
Version: 1.0.0
Last Updated: [YYYY-MM-DD]
Project: [PROJECT_NAME]
-->

## 1. Architectural Principles

| Principle               | Description                        | Priority | Implementation          |
| ----------------------- | ---------------------------------- | -------- | ----------------------- |
| **Design Pattern**      | [ARCHITECTURE_PATTERN]             | MUST     | [PATTERN_EXAMPLE]       |
| Service Responsibility  | [SERVICE_RESPONSIBILITY_PRINCIPLE] | MUST     | One concern per service |
| State Management        | [STATE_MANAGEMENT_APPROACH]        | MUST     | Stateless/Event-driven  |
| Component Separation    | [COMPONENT_SEPARATION_PRINCIPLE]   | MUST     | Clear boundaries        |
| Data Access Pattern     | [DATA_ACCESS_PATTERN]              | MUST     | Repository/Gateway      |
| Performance Constraints | [PERFORMANCE_REQUIREMENTS]         | SHOULD   | Response time limits    |

---

## 2. Service Architecture

| Component        | Responsibility                   | Pattern              | Notes                   |
| ---------------- | -------------------------------- | -------------------- | ----------------------- |
| **Handlers**     | [HANDLER_RESPONSIBILITIES]       | [HANDLER_PATTERN]    | Entry point, thin logic |
| **Services**     | [SERVICE_LAYER_RESPONSIBILITIES] | [SERVICE_PATTERN]    | Business logic          |
| **Repositories** | [REPOSITORY_RESPONSIBILITIES]    | [REPOSITORY_PATTERN] | Data access abstraction |
| **Models/DTOs**  | [MODEL_RESPONSIBILITIES]         | [MODEL_PATTERN]      | Data structures         |
| **Validators**   | [VALIDATOR_RESPONSIBILITIES]     | [VALIDATOR_PATTERN]  | Input validation        |
| **Middleware**   | [MIDDLEWARE_RESPONSIBILITIES]    | [MIDDLEWARE_PATTERN] | Cross-cutting concerns  |

### Service Flow Pattern

| Step | Layer      | Action                            | Validation         |
| ---- | ---------- | --------------------------------- | ------------------ |
| 1    | Handler    | Parse request, validate structure | Schema validation  |
| 2    | Service    | Execute business logic            | Business rules     |
| 3    | Repository | Persist/retrieve data             | Data integrity     |
| 4    | Handler    | Format response, handle errors    | Response structure |

### CRUD Operations Standard

| HTTP Method | Route Pattern   | Service Method  | Expected Behavior |
| ----------- | --------------- | --------------- | ----------------- |
| POST        | `/resource`     | [CREATE_METHOD] | [CREATE_BEHAVIOR] |
| GET         | `/resource/:id` | [READ_METHOD]   | [READ_BEHAVIOR]   |
| GET         | `/resource`     | [LIST_METHOD]   | [LIST_BEHAVIOR]   |
| PATCH       | `/resource/:id` | [UPDATE_METHOD] | [UPDATE_BEHAVIOR] |
| DELETE      | `/resource/:id` | [DELETE_METHOD] | [DELETE_BEHAVIOR] |

---

## 3. Database Design Standards

| Guideline             | Requirement                        | Priority    | When Required               |
| --------------------- | ---------------------------------- | ----------- | --------------------------- |
| **Primary Keys**      | [KEY_DEFINITION_REQUIREMENTS]      | MUST        | All tables                  |
| Partition Key Design  | [PARTITION_KEY_STRATEGY]           | MUST        | Avoid hot partitions        |
| Sort Key Design       | [SORT_KEY_STRATEGY]                | SHOULD      | When query patterns need it |
| **Secondary Indexes** | [SECONDARY_INDEX_CRITERIA]         | CONDITIONAL | Alternative query patterns  |
| Index Justification   | [INDEX_JUSTIFICATION_REQUIREMENTS] | MUST        | Before creating GSI/index   |
| **TTL Configuration** | [TTL_REQUIREMENTS]                 | MUST        | Transient data              |
| TTL Documentation     | [TTL_DOCUMENTATION_REQUIREMENTS]   | MUST        | When TTL is used            |
| **Capacity Mode**     | [CAPACITY_MODE_SELECTION]          | MUST        | All tables                  |
| On-Demand Mode        | Use for unpredictable traffic      | SHOULD      | Variable load patterns      |
| Provisioned Mode      | Use for predictable traffic        | SHOULD      | Consistent load patterns    |

### Database Prohibitions (WON'T)

- Create indexes without justification
- Use TTL without documenting data lifecycle
- Over-provision capacity
- Create hot partitions
- Skip capacity mode justification

---

## 4. API Design Standards

| Standard Area           | Requirement                        | Priority | Validation              |
| ----------------------- | ---------------------------------- | -------- | ----------------------- |
| **Security Headers**    | [SECURITY_HEADERS_REQUIREMENTS]    | MUST     | Automated scanning      |
| **CORS Policy**         | [CORS_CONFIGURATION]               | MUST     | Security review         |
| **Rate Limiting**       | [RATE_LIMITING_STRATEGY]           | MUST     | Load testing            |
| **Request Size Limits** | [REQUEST_SIZE_LIMITS]              | MUST     | API gateway config      |
| **Error Responses**     | [ERROR_RESPONSE_FORMAT]            | MUST     | API testing             |
| Error Localization      | [ERROR_LOCALIZATION_POLICY]        | SHOULD   | i18n review             |
| **Authentication**      | [API_AUTHENTICATION_METHOD]        | MUST     | Security audit          |
| Token Validation        | [TOKEN_VALIDATION_REQUIREMENTS]    | MUST     | Every request           |
| **Authorization**       | [AUTHORIZATION_MECHANISM]          | MUST     | Scope-based             |
| **Input Validation**    | [INPUT_VALIDATION_STANDARDS]       | MUST     | Schema validation       |
| Output Sanitization     | [OUTPUT_SANITIZATION_REQUIREMENTS] | MUST     | XSS prevention          |
| **Content-Type Check**  | [CONTENT_TYPE_VALIDATION]          | MUST     | Request validation      |
| **API Versioning**      | [API_VERSIONING_STRATEGY]          | MUST     | URL path or header      |
| **Throttling**          | [THROTTLING_REQUIREMENTS]          | MUST     | DDoS protection         |
| Geographic Controls     | [GEO_ACCESS_CONTROLS]              | SHOULD   | Compliance requirements |

---

## 5. Security Architecture

| Security Layer            | Requirement                          | Priority | Implementation        |
| ------------------------- | ------------------------------------ | -------- | --------------------- |
| **Defense in Depth**      | [DEFENSE_IN_DEPTH_STRATEGY]          | MUST     | Multiple layers       |
| **Zero Trust**            | [ZERO_TRUST_PRINCIPLES]              | MUST     | Verify everything     |
| Network Segmentation      | [NETWORK_SEGMENTATION_REQUIREMENTS]  | MUST     | Isolated environments |
| **Identity Verification** | [IDENTITY_VERIFICATION_REQUIREMENTS] | MUST     | Every access          |
| **Security Monitoring**   | [SECURITY_MONITORING_REQUIREMENTS]   | MUST     | Real-time detection   |
| Threat Detection          | [THREAT_DETECTION_MECHANISM]         | MUST     | Automated alerts      |
| **Audit Logging**         | [AUDIT_LOGGING_REQUIREMENTS]         | MUST     | Immutable logs        |
| Security Metrics          | [SECURITY_METRICS_COLLECTION]        | SHOULD   | Dashboard monitoring  |
| **Encryption**            | [ENCRYPTION_STANDARDS]               | MUST     | At rest & in transit  |
| Key Management            | [KEY_MANAGEMENT_REQUIREMENTS]        | MUST     | Secure key rotation   |
