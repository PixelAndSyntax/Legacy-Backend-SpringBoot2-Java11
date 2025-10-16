# Constitution Security Standards

<!--
Section: security
Priority: critical
Applies to: all projects (especially backend)
Dependencies: [core]
Version: 1.0.0
Last Updated: [YYYY-MM-DD]
Project: [PROJECT_NAME]
-->

## 1. Core Security Principles

| Principle              | Requirement                 | Priority | Enforcement         |
| ---------------------- | --------------------------- | -------- | ------------------- |
| **Least Privilege**    | [ACCESS_CONTROL_PRINCIPLE]  | MUST     | IAM review          |
| **Zero Trust**         | Verify every access request | MUST     | Security audit      |
| **Defense in Depth**   | Multiple security layers    | MUST     | Architecture review |
| **Fail Secure**        | [FAIL_SECURE_REQUIREMENTS]  | MUST     | Code review         |
| **Complete Mediation** | Check every access          | MUST     | Security testing    |

---

## 2. Authentication & Authorization

| Security Control      | Requirement                   | Priority | Validation           |
| --------------------- | ----------------------------- | -------- | -------------------- |
| **Authentication**    | [AUTH_MECHANISM_REQUIREMENTS] | MUST     | Security review      |
| Multi-Factor Auth     | [MFA_REQUIREMENTS]            | MUST     | Admin access         |
| Token Management      | [TOKEN_MANAGEMENT_POLICY]     | MUST     | Automated validation |
| Session Expiration    | [SESSION_TIMEOUT_POLICY]      | MUST     | Security config      |
| **Authorization**     | [AUTHZ_MECHANISM]             | MUST     | Permission testing   |
| RBAC Implementation   | [ROLE_BASED_ACCESS_CONTROL]   | MUST     | Architecture review  |
| Permission Boundaries | [PERMISSION_BOUNDARY_POLICY]  | MUST     | Code review          |
| Token Rotation        | [TOKEN_ROTATION_REQUIREMENTS] | MUST     | Automated            |

---

## 3. Data Protection

| Protection Type           | Requirement                          | Priority | Implementation                          |
| ------------------------- | ------------------------------------ | -------- | --------------------------------------- |
| **Encryption at Rest**    | [ENCRYPTION_AT_REST_REQUIREMENTS]    | MUST     | Storage encryption                      |
| **Encryption in Transit** | [ENCRYPTION_IN_TRANSIT_REQUIREMENTS] | MUST     | TLS 1.3+                                |
| **PII Handling**          | [PII_HANDLING_POLICY]                | MUST     | Data classification                     |
| Data Minimization         | Collect only necessary data          | MUST     | Privacy review                          |
| **Data Classification**   | [DATA_CLASSIFICATION_STANDARDS]      | MUST     | Public/Internal/Confidential/Restricted |
| Data Retention            | [DATA_RETENTION_POLICY]              | MUST     | Compliance review                       |
| Secure Deletion           | [SECURE_DELETION_REQUIREMENTS]       | MUST     | Data lifecycle                          |
| **Key Management**        | [KEY_MANAGEMENT_REQUIREMENTS]        | MUST     | Key rotation schedule                   |

---

## 4. Input Validation & Output Sanitization

| Security Control         | Requirement                        | Priority | Protection Against   |
| ------------------------ | ---------------------------------- | -------- | -------------------- |
| **Input Validation**     | [INPUT_VALIDATION_SECURITY]        | MUST     | Injection attacks    |
| SQL Injection Prevention | Use parameterized queries          | MUST     | SQLi                 |
| XSS Prevention           | [XSS_PREVENTION_MEASURES]          | MUST     | XSS attacks          |
| Command Injection        | [COMMAND_INJECTION_PREVENTION]     | MUST     | OS command injection |
| **Output Encoding**      | [OUTPUT_ENCODING_REQUIREMENTS]     | MUST     | XSS, data leakage    |
| CSP Headers              | [CSP_HEADER_REQUIREMENTS]          | MUST     | XSS, clickjacking    |
| Path Traversal           | [PATH_TRAVERSAL_PREVENTION]        | MUST     | File access attacks  |
| **Type Validation**      | Strict type checking on all inputs | MUST     | Type confusion       |

---

## 5. Secret Management

| Secret Type              | Requirement                   | Priority | Storage Method       |
| ------------------------ | ----------------------------- | -------- | -------------------- |
| **API Keys**             | [API_KEY_MANAGEMENT]          | MUST     | Secrets manager      |
| **Database Credentials** | [DB_CREDENTIAL_MANAGEMENT]    | MUST     | Secrets manager      |
| **Encryption Keys**      | [ENCRYPTION_KEY_MANAGEMENT]   | MUST     | KMS/HSM              |
| **JWT Secrets**          | [JWT_SECRET_REQUIREMENTS]     | MUST     | Secrets manager      |
| **Secret Rotation**      | [SECRET_ROTATION_POLICY]      | MUST     | Automated rotation   |
| Environment Separation   | [ENV_SEPARATION_REQUIREMENTS] | MUST     | Dev/Stage/Prod split |

### Secret Prohibitions (WON'T)

- Never log secrets (tokens, passwords, keys, PKCE verifiers)
- Never commit secrets to version control
- Never store secrets in plaintext
- Never expose secrets in error messages
- Never transmit secrets in URLs

---

## 6. Security Logging & Monitoring

| Event Type                 | Logging Requirement              | Priority | Retention Period       |
| -------------------------- | -------------------------------- | -------- | ---------------------- |
| **Authentication Events**  | [AUTH_LOGGING_REQUIREMENTS]      | MUST     | [LOG_RETENTION_PERIOD] |
| Failed Login Attempts      | Log all failures with context    | MUST     | [LOG_RETENTION_PERIOD] |
| **Authorization Failures** | [AUTHZ_LOGGING_REQUIREMENTS]     | MUST     | [LOG_RETENTION_PERIOD] |
| Privilege Escalation       | Log all attempts                 | MUST     | [LOG_RETENTION_PERIOD] |
| **Data Access**            | [DATA_ACCESS_LOGGING]            | MUST     | [LOG_RETENTION_PERIOD] |
| Privileged Operations      | Log all admin actions            | MUST     | [LOG_RETENTION_PERIOD] |
| **Security Events**        | [SECURITY_EVENT_LOGGING]         | MUST     | [LOG_RETENTION_PERIOD] |
| Anomaly Detection          | [ANOMALY_DETECTION_REQUIREMENTS] | SHOULD   | Real-time alerting     |

### Logging Prohibitions (WON'T)

- Never log secrets or credentials
- Never log full credit card numbers
- Never log PII without justification
- Never log sensitive cryptographic material

---

## 7. Network Security

| Control                  | Requirement                         | Priority | Implementation       |
| ------------------------ | ----------------------------------- | -------- | -------------------- |
| **Network Segmentation** | [NETWORK_SEGMENTATION_REQUIREMENTS] | MUST     | VPC/subnet isolation |
| **Firewall Rules**       | [FIREWALL_RULES_POLICY]             | MUST     | Default deny         |
| **TLS Configuration**    | Minimum TLS 1.3                     | MUST     | All external comms   |
| Certificate Management   | [CERTIFICATE_MANAGEMENT_POLICY]     | MUST     | Auto-renewal         |
| **DDoS Protection**      | [DDOS_PROTECTION_REQUIREMENTS]      | MUST     | Rate limiting        |
| API Rate Limiting        | [RATE_LIMITING_POLICY]              | MUST     | Per endpoint         |
| **CORS Policy**          | [CORS_CONFIGURATION]                | MUST     | Restrictive origins  |

---

## 8. Vulnerability Management

| Activity                | Requirement                        | Frequency        | Priority |
| ----------------------- | ---------------------------------- | ---------------- | -------- |
| **Dependency Scanning** | [DEPENDENCY_SCANNING_REQUIREMENTS] | Every build      | MUST     |
| Vulnerability Patching  | [SECURITY_UPDATE_POLICY]           | Within [X] hours | MUST     |
| **Code Scanning**       | Static analysis security testing   | Every commit     | MUST     |
| **Penetration Testing** | [PENETRATION_TESTING_SCHEDULE]     | Quarterly        | SHOULD   |
| Security Audits         | [SECURITY_AUDIT_REQUIREMENTS]      | Annually         | SHOULD   |
| Threat Modeling         | [THREAT_MODELING_REQUIREMENTS]     | Per feature      | SHOULD   |
