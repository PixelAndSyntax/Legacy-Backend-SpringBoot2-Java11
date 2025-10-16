# Constitution Observability Standards

<!--
Section: observability
Priority: high
Applies to: backend, infrastructure
Dependencies: [core]
Version: 1.0.0
Last Updated: [YYYY-MM-DD]
Project: [PROJECT_NAME]
-->

## 1. Logging Standards

### Mandatory Log Fields

| Field         | Type   | Format          | Required | Description                    |
| ------------- | ------ | --------------- | -------- | ------------------------------ |
| correlationId | string | UUID            | MUST     | Trace requests across services |
| timestamp     | string | ISO 8601        | MUST     | Log entry timestamp            |
| level         | enum   | INFO/WARN/ERROR | MUST     | Log severity level             |
| service       | string | Service name    | MUST     | Service generating the log     |
| message       | string | Descriptive     | MUST     | Human-readable description     |

### Optional Context Fields

| Field     | Type   | When Required          | Description                     |
| --------- | ------ | ---------------------- | ------------------------------- |
| operation | string | Available              | Function/method being executed  |
| userId    | string | User context exists    | User identifier                 |
| sessionId | string | Session context exists | Session identifier              |
| requestId | string | HTTP/API context       | Request identifier              |
| duration  | number | Operation complete     | Execution time (milliseconds)   |
| outcome   | enum   | Operation complete     | success/failure/timeout/partial |

### Error-Specific Fields

| Field        | Type   | Required When  | Environment   | Description            |
| ------------ | ------ | -------------- | ------------- | ---------------------- |
| errorCode    | string | level == ERROR | All           | Application error code |
| errorMessage | string | level == ERROR | All           | Error description      |
| errorType    | string | level == ERROR | All           | Error category         |
| stackTrace   | string | level == ERROR | Dev/Test only | Technical stack trace  |

### Logging Patterns

| Pattern         | When                | Example                                                | Priority |
| --------------- | ------------------- | ------------------------------------------------------ | -------- |
| Entry Logging   | Operation starts    | `LOG.INFO("Operation started", {correlationId})`       | MUST     |
| Success Logging | Operation completes | `LOG.INFO("Operation completed", {outcome, duration})` | SHOULD   |
| Error Logging   | Operation fails     | `LOG.ERROR("Operation failed", {errorCode})`           | MUST     |

### Logging Prohibitions (WON'T)

- Never log secrets (passwords, tokens, keys, API keys)
- Never log PII without justification
- Never use string concatenation for log messages
- Never skip correlation ID
- Never log sensitive cryptographic material

---

## 2. Log Implementation Standards

| Requirement            | Description                    | Priority | Validation            |
| ---------------------- | ------------------------------ | -------- | --------------------- |
| **Structured Format**  | Use JSON structured logging    | MUST     | Automated linting     |
| Correlation ID         | Include in every log entry     | MUST     | Automated scanning    |
| Consistent Fields      | [CONSISTENT_FIELD_NAMING]      | MUST     | Schema validation     |
| **Environment Config** | [LOG_ENVIRONMENT_REQUIREMENTS] | MUST     | Deployment check      |
| Test Environment       | Console output, same format    | MUST     | Test validation       |
| Production Environment | [PRODUCTION_LOG_DESTINATION]   | MUST     | Infrastructure config |
| Log Levels             | INFO/DEBUG/WARN/ERROR          | MUST     | Code review           |
| **Log Sampling**       | [LOG_SAMPLING_STRATEGY]        | SHOULD   | Performance tuning    |

---

## 3. Metrics Standards

### Metric Categories

| Category                | Examples                           | Collection | Priority |
| ----------------------- | ---------------------------------- | ---------- | -------- |
| **Business Metrics**    | [BUSINESS_METRICS_LIST]            | Real-time  | MUST     |
| User Activity           | Signups, logins, transactions      | Real-time  | MUST     |
| Revenue Metrics         | Transaction value, conversions     | Real-time  | SHOULD   |
| **System Metrics**      | [SYSTEM_METRICS_LIST]              | Real-time  | MUST     |
| Request Latency         | p50, p95, p99 response times       | Continuous | MUST     |
| Error Rates             | 4xx, 5xx error percentages         | Continuous | MUST     |
| Throughput              | Requests per second                | Continuous | MUST     |
| **Performance Metrics** | [PERFORMANCE_METRICS_LIST]         | Continuous | MUST     |
| Database Latency        | Query execution times              | Continuous | MUST     |
| External API Latency    | Third-party service response times | Continuous | SHOULD   |
| Resource Usage          | CPU, memory, disk utilization      | Continuous | MUST     |

### Metric Requirements

| Requirement           | Description                       | Priority | Notes                        |
| --------------------- | --------------------------------- | -------- | ---------------------------- |
| **Consistent Naming** | [METRIC_NAMING_CONVENTION]        | MUST     | snake_case or dot.notation   |
| **Appropriate Tags**  | [METRIC_TAGGING_STRATEGY]         | MUST     | Environment, service, region |
| **Thresholds**        | [ALERT_THRESHOLD_REQUIREMENTS]    | MUST     | Define SLOs/SLAs             |
| Cardinality Control   | Avoid high-cardinality dimensions | MUST     | Performance impact           |
| **Unit Consistency**  | Use standard units (ms, bytes, %) | MUST     | Data interpretation          |

---

## 4. Distributed Tracing Standards

| Requirement           | Description                             | Priority | Implementation        |
| --------------------- | --------------------------------------- | -------- | --------------------- |
| **Trace Propagation** | [TRACE_CONTEXT_PROPAGATION]             | MUST     | W3C Trace Context     |
| **Span Creation**     | Create spans for significant operations | MUST     | Function-level        |
| Span Attributes       | [SPAN_ATTRIBUTE_REQUIREMENTS]           | MUST     | Semantic conventions  |
| **Sampling Strategy** | [TRACE_SAMPLING_STRATEGY]               | MUST     | Adaptive sampling     |
| Sampling Rate         | [SAMPLING_RATE_CONFIGURATION]           | MUST     | Environment-specific  |
| **Error Tracking**    | Capture errors in spans                 | MUST     | Exception details     |
| Trace Completeness    | Monitor for dropped spans               | SHOULD   | Observability metrics |

### Tracing Best Practices

| Practice             | Requirement                      | Priority |
| -------------------- | -------------------------------- | -------- |
| Semantic Conventions | Follow OpenTelemetry standards   | SHOULD   |
| Context Enrichment   | Add relevant business context    | SHOULD   |
| Performance Impact   | Minimize tracing overhead        | MUST     |
| Trace Analysis       | Regular review of trace patterns | SHOULD   |

---

## 5. Alerting Standards

| Alert Type          | Condition                    | Severity | Response Time      |
| ------------------- | ---------------------------- | -------- | ------------------ |
| **Error Rate**      | [ERROR_RATE_THRESHOLD]       | Critical | Immediate          |
| **Latency**         | [LATENCY_THRESHOLD_P95]      | High     | 15 minutes         |
| **Availability**    | [AVAILABILITY_THRESHOLD]     | Critical | Immediate          |
| **Resource Usage**  | [RESOURCE_USAGE_THRESHOLD]   | Medium   | 30 minutes         |
| **Security Events** | [SECURITY_EVENT_CRITERIA]    | Critical | Immediate          |
| **Custom Business** | [BUSINESS_METRIC_THRESHOLDS] | Variable | Defined per metric |

### Alert Requirements

| Requirement       | Description                   | Priority |
| ----------------- | ----------------------------- | -------- |
| Actionable Alerts | Include context for response  | MUST     |
| Alert Routing     | Route to appropriate teams    | MUST     |
| Escalation Policy | Define escalation paths       | MUST     |
| Alert Suppression | Prevent alert fatigue         | SHOULD   |
| Runbook Links     | Link to resolution procedures | SHOULD   |
