# Implementation Audit Report

**Audit Date**: [YYYY-MM-DD]  
**Feature**: [FEATURE NAME]  
**Feature Branch**: [feature-name]  
**Overall Quality Score**: [X]%  
**Production Ready**: [Yes/No]  
**Input**: Implementation in `[feature-directory]` compared against specification documents

## Execution Flow (main)

```
1. Load specification documents from feature directory:
   → spec.md: Extract all functional requirements (FR-XXX)
   → plan.md: Extract technical design, architecture decisions
   → tasks.md: Extract all tasks and completion status
   → If Reference Context exists: Load patterns and standards
   → If any doc missing: ERROR "Incomplete specification for audit"
2. Scan implementation files:
   → Identify all source files from plan.md structure
   → Read implementation code
   → Check for tests (unit, integration, contract)
   → Examine error handling and validation
   → Review documentation and comments
3. Audit requirements coverage:
   → For each FR in spec.md: Verify implementation exists
   → Test against acceptance scenarios
   → Check edge cases are handled
   → Mark as: Implemented, Partial, Missing
4. Audit task completion:
   → Count tasks marked [X] vs total tasks
   → Identify incomplete tasks
   → Check for skipped test tasks
5. Audit code quality:
   → Code organization and structure
   → Naming conventions consistency
   → Documentation completeness
   → Error handling robustness
   → Security issues (secrets, vulnerabilities)
6. Audit testing:
   → Test coverage for requirements
   → Integration test scenarios
   → Edge case coverage
   → Contract test completeness
7. Compare against technical plan:
   → Architecture alignment
   → Libraries and dependencies used correctly
   → Performance considerations addressed
8. Prioritize issues:
   → Critical: Missing core functionality, security issues
   → High: Incomplete requirements, missing tests
   → Medium: Code quality, minor deviations
   → Low: Style, optimizations
9. Calculate metrics and write to [feature-dir]/AUDIT.md
10. Return: SUCCESS with quality score and production readiness
```

---

## ⚡ Audit Guidelines

- ✅ Focus on ALIGNMENT between specification and implementation
- ✅ Verify COMPLETENESS of requirements coverage
- ✅ Assess QUALITY of code and testing
- ❌ Don't nitpick style if it doesn't affect quality

### Severity Levels

- **Critical**: Missing core functionality, security vulnerabilities, broken acceptance criteria
- **High**: Incomplete requirements, significant deviations, missing tests
- **Medium**: Code quality issues, minor deviations, documentation gaps
- **Low**: Style inconsistencies, optimization opportunities

### For AI Generation

When creating this audit report:

1. **Be objective**: Use measurable criteria from specification
2. **Show evidence**: Reference exact requirements and file paths
3. **Test acceptance criteria**: Actually validate each scenario
4. **Check edge cases**: Verify error scenarios from spec.md
5. **Common audit findings**:
   - Missing requirements implementation
   - Incomplete task execution
   - Insufficient test coverage
   - Unhandled edge cases
   - Security vulnerabilities
   - Performance issues
   - Documentation gaps

---

## Summary Metrics

### Quality Scores

- **Overall Quality Score**: [X]%
- **Requirements Coverage**: [X]% ([Y] of [Z] requirements implemented)
- **Task Completion**: [X]% ([Y] of [Z] tasks complete)
- **Test Coverage**: [X]% ([Y] of [Z] scenarios tested)
- **Code Quality**: [Excellent/Good/Fair/Poor]

### Production Readiness

- ✅ **Ready for Production**: [Yes/No]
- **Blockers**: [List critical issues blocking production]
- **Recommended Actions**: [Next steps before release]

### Issue Summary

- **Total Issues**: [X]
  - Critical: [X]
  - High: [X]
  - Medium: [X]
  - Low: [X]

---

## Requirements Coverage

### Implemented Requirements ✅

- **FR-001**: [Requirement description]
  - **Status**: Fully Implemented
  - **Files**: [file paths]
  - **Tests**: [test file paths]

### Partially Implemented Requirements ⚠️

- **FR-XXX**: [Requirement description]
  - **Status**: Partially Implemented
  - **What's Missing**: [specific gaps]
  - **Files**: [file paths]
  - **Priority**: [High/Medium]
  - **Remediation**: [specific steps needed]

### Missing Requirements ❌

- **FR-XXX**: [Requirement description]
  - **Status**: Not Implemented
  - **Expected Location**: [where it should be]
  - **Priority**: [Critical/High]
  - **Remediation**: [implementation steps]

---

## Acceptance Criteria Validation

### Scenario 1: [Scenario Name]

- **Given**: [initial state from spec]
- **When**: [action from spec]
- **Then**: [expected outcome from spec]
- **Result**: ✅ Pass / ❌ Fail / ⚠️ Partial
- **Evidence**: [test results or manual validation]
- **Issues**: [if any]

### Scenario 2: [Scenario Name]

[Repeat structure for each acceptance scenario]

---

## Task Completion Analysis

### Completed Tasks ✅

- [x] **T001**: [Task description]
  - **Files**: [file paths]
  - **Quality**: Good

### Incomplete Tasks ⚠️

- [ ] **TXXX**: [Task description]
  - **Status**: Not completed or partially done
  - **Impact**: [How this affects functionality]
  - **Priority**: [High/Medium]

### Skipped Tasks ❌

- [ ] **TXXX**: [Task description marked with strikethrough or noted as skipped]
  - **Reason**: [Why was it skipped]
  - **Impact**: [Critical if test task skipped]

---

## Critical Issues

### CRIT-001: [Issue Title]

- **Severity**: Critical
- **Requirement**: [FR-XXX or acceptance scenario reference]
- **Description**: [Detailed issue description]
- **Current State**: [What currently exists or is missing]
- **Expected State**: [What spec.md requires]
- **Impact**: [Business/technical impact]
- **Files Affected**: [file paths]
- **Remediation**:
  1. [Step 1]
  2. [Step 2]
- **Effort**: [Estimated hours/days]

---

## High Priority Issues

### HIGH-001: [Issue Title]

- **Severity**: High
- **Requirement**: [Specification reference]
- **Description**: [Issue description]
- **Current State**: [Current situation]
- **Expected State**: [Required state]
- **Files Affected**: [file paths]
- **Remediation**: [Fix steps]
- **Effort**: [Time estimate]

---

## Medium Priority Issues

### MED-001: [Issue Title]

- **Severity**: Medium
- **Requirement**: [Specification reference]
- **Description**: [Issue description]
- **Remediation**: [Fix steps]
- **Effort**: [Time estimate]

---

## Low Priority Issues

### LOW-001: [Issue Title]

- **Severity**: Low
- **Description**: [Issue description]
- **Remediation**: [Fix steps]

---

## Code Quality Assessment

### Strengths ✅

- [List positive aspects of implementation]
- [Good patterns or practices observed]
- [Well-implemented features]

### Areas for Improvement 📈

- **Code Organization**: [Observations about structure]
- **Naming Conventions**: [Consistency issues if any]
- **Documentation**: [Missing or inadequate documentation]
- **Error Handling**: [Gaps in error scenarios]
- **Performance**: [Performance considerations]

### Security Concerns 🔒

- [Any security issues found]
- [Exposed secrets or credentials]
- [Vulnerable dependencies]
- [Missing authentication/authorization]

---

## Testing Assessment

### Test Coverage

- **Unit Tests**: [Coverage percentage if measurable]
- **Integration Tests**: [Number of scenarios covered]
- **Contract Tests**: [API endpoint coverage]
- **Edge Case Tests**: [Coverage of error scenarios]

### Testing Gaps

- [ ] Missing tests for [specific functionality]
- [ ] Edge case [X] not tested
- [ ] No integration test for [scenario]

### Test Quality

- [Assessment of test quality and effectiveness]
- [Any brittle or unreliable tests]

---

## Technical Alignment

### Architecture Compliance

- ✅ Follows plan.md architecture: [Yes/No with details]
- ✅ Uses specified tech stack: [Yes/No with details]
- ✅ Implements patterns correctly: [Yes/No with details]

### Deviations from Plan

- [Any architectural deviations from plan.md]
- [Justification if deviation makes sense]
- [Concerns if deviation is problematic]

---

## Recommendations

### Before Production Release

1. **Critical Actions**:

   - [Action 1 with specific steps]
   - [Action 2 with specific steps]

2. **High Priority Actions**:

   - [Action items to address high priority issues]

3. **Testing Recommendations**:
   - [Additional testing needed]
   - [Manual testing scenarios]

### Future Improvements

- [Medium and low priority improvements]
- [Optimization opportunities]
- [Technical debt to address]

---

## Sign-Off Criteria

### Ready for Production Checklist

- [ ] All critical issues resolved
- [ ] All high priority issues resolved or have mitigation plan
- [ ] Requirements coverage ≥ 95%
- [ ] All acceptance scenarios pass
- [ ] Test coverage ≥ [target]%
- [ ] No security vulnerabilities
- [ ] Documentation complete
- [ ] Performance meets requirements

### Approval Status

- **QA Approval**: [Pending/Approved/Rejected]
- **Technical Lead Approval**: [Pending/Approved/Rejected]
- **Sign-Off Date**: [Date when approved for production]

---

## Next Actions

### Immediate Actions (Next 1-3 Days)

1. [Critical issue with highest impact]
2. [Next critical action]

### Short-term Actions (This Week)

1. [High priority items]
2. [Required testing]

### Follow-up Audit

- Re-audit recommended after: [Critical/High priority fixes]
- Next audit date: [Date]

---

## Audit Notes

### Methodology

- Audit conducted by: [Automated/AI-assisted/Manual]
- Audit scope: [Full/Partial with details]
- Testing approach: [Automated tests/Manual validation/Both]

### Limitations

- [Any limitations of the audit]
- [Areas not covered]
- [Assumptions made]

### Positive Findings

- [Highlight excellent implementation aspects]
- [Commendations for particularly good work]
- [Patterns worth replicating]

---

## Appendix

### Files Audited

- [List all files reviewed]
- [Source files count]
- [Test files count]

### Requirements Traceability Matrix

| Requirement | Status | Implementation | Tests   | Issues   |
| ----------- | ------ | -------------- | ------- | -------- |
| FR-001      | ✅     | [files]        | [tests] | None     |
| FR-002      | ⚠️     | [files]        | [tests] | MED-001  |
| FR-003      | ❌     | Missing        | Missing | CRIT-001 |

### Test Results Summary

| Test Suite        | Total | Passed | Failed | Skipped |
| ----------------- | ----- | ------ | ------ | ------- |
| Unit Tests        | [X]   | [X]    | [X]    | [X]     |
| Integration Tests | [X]   | [X]    | [X]    | [X]     |
| Contract Tests    | [X]   | [X]    | [X]    | [X]     |
