# Constitutional Drift Report

**Detection Date**: [YYYY-MM-DD]  
**Constitution Version**: [X.Y.Z]  
**Project Status**: [Compliant/Partially Compliant/Non-Compliant]  
**Drift Score**: [percentage of principles with drift]  
**Input**: Project state at `.` compared against modular constitution files in `.specify/memory/constitution/`

## Execution Flow (main)

```
1. Load modular constitution files from .specify/memory/constitution/:
   → core.md - Technology stack, coding standards
   → architecture.md - Service patterns, design principles
   → testing.md - Test strategy, coverage requirements
   → security.md - Security policies
   → observability.md - Logging, monitoring standards
   → optional.md - Project-specific standards
   → If not found: ERROR "Constitution not found. Run @sdd-init first"
   → Extract: all principles, requirements, standards from each file
2. Scan project structure and files:
   → README.md, package.json, pyproject.toml, etc.
   → Check documentation completeness
   → Examine code structure and organization
   → Review testing setup and coverage
   → Scan for security issues (secrets, vulnerabilities)
   → Verify coding standards compliance
   → Assess CI/CD and deployment practices
3. Compare against each constitutional principle:
   → Identify gaps and violations
   → Detect missing files, configs, or practices
   → Flag security drift (exposed secrets, vulnerable deps)
   → Check coding standard drift (linting, formatting)
   → Note documentation gaps
   → Assess testing coverage drift
4. Prioritize drift items:
   → Critical: Security vulnerabilities, exposed secrets
   → High: Missing controls, standard violations
   → Medium: Documentation gaps, incomplete testing
   → Low: Style inconsistencies, optimizations
5. Calculate drift score:
   → (Principles with drift / Total principles) × 100
   → Include severity weighting
6. Generate drift report sections:
   → One section per constitutional principle (organized by module)
   → Group items by priority within each principle
   → Include clear descriptions and requirements
   → Suggest implementation approaches
7. Write output to `.specify/specs/CONSTITUTION_DRIFT.md`:
   → Use absolute path from repository root
   → If file exists, overwrite with new report
   → Ensure .specify/specs/ directory exists
8. Return: SUCCESS with summary statistics and file location
```

---

## ⚡ Report Guidelines

- ✅ Focus on GAPS between current state and constitution
- ✅ Provide actionable, specific remediation steps
- ✅ Reference exact constitutional requirements
- ❌ Don't create busywork - only flag real drift

### Priority Levels

- **Critical**: Security issues, compliance violations, broken builds
- **High**: Missing controls, standard violations, significant gaps
- **Medium**: Documentation issues, incomplete coverage, minor violations
- **Low**: Style inconsistencies, optimizations, nice-to-haves

### For AI Generation

When creating this drift report:

1. **Be specific**: Reference exact file paths and line numbers where applicable
2. **Show evidence**: Quote the constitutional requirement being violated
3. **Estimate effort**: Provide realistic time estimates (hours/days)
4. **Suggest fixes**: Include concrete implementation steps
5. **Common drift areas**:
   - Missing or outdated documentation
   - Exposed secrets or hardcoded credentials
   - Vulnerable dependencies
   - Insufficient test coverage
   - Linting/formatting inconsistencies
   - Missing CI/CD workflows
   - Unimplemented security controls

---

## Summary Statistics

### Overall Metrics

- **Total Principles**: [X]
- **Principles with Drift**: [X]
- **Drift Score**: [X]%
- **Total Drift Items**: [X]
  - Critical: [X]
  - High: [X]
  - Medium: [X]
  - Low: [X]

### Compliance Status

- ✅ **Compliant Principles**: [X] ([list])
- ⚠️ **Partially Compliant**: [X] ([list])
- ❌ **Non-Compliant**: [X] ([list])

### Recommended Timeline

- **Critical items**: [Immediate/This week]
- **High priority items**: [Within 2 weeks]
- **Medium priority items**: [This sprint/month]
- **Low priority items**: [Backlog]

---

## Principle 1: [Principle Name from Constitution]

**Status**: [Compliant/Partially Compliant/Non-Compliant]  
**Drift Items**: [X]

### Constitutional Requirement

> [Quote the exact principle from the constitution]

### Critical Drift

- [ ] **[DRIFT-001]**: [Specific issue description]
  - **Requirement**: [Which constitutional requirement is violated]
  - **Current State**: [What the project currently has/does]
  - **Required State**: [What the constitution requires]
  - **Impact**: [Why this matters - security, reliability, etc.]
  - **Remediation**: [Specific steps to fix]
  - **Effort**: [Estimated hours/days]
  - **Files**: [Affected file paths]

### High Priority Drift

- [ ] **[DRIFT-002]**: [Specific issue description]
  - **Requirement**: [Constitutional reference]
  - **Current State**: [Current situation]
  - **Required State**: [Target state]
  - **Impact**: [Business/technical impact]
  - **Remediation**: [Fix steps]
  - **Effort**: [Time estimate]
  - **Files**: [File paths]

### Medium Priority Drift

- [ ] **[DRIFT-003]**: [Specific issue description]
  - **Requirement**: [Constitutional reference]
  - **Current State**: [Current situation]
  - **Required State**: [Target state]
  - **Remediation**: [Fix steps]
  - **Effort**: [Time estimate]

### Low Priority Drift

- [ ] **[DRIFT-004]**: [Specific issue description]
  - **Requirement**: [Constitutional reference]
  - **Remediation**: [Fix steps]
  - **Effort**: [Time estimate]

---

## Principle 2: [Principle Name from Constitution]

**Status**: [Compliant/Partially Compliant/Non-Compliant]  
**Drift Items**: [X]

### Constitutional Requirement

> [Quote the exact principle from the constitution]

[Repeat structure from Principle 1 for each principle in the constitution]

---

## Action Plan

### Immediate Actions (Next 1-3 Days)

1. [Critical drift item with highest impact]
2. [Next critical drift item]

### Short-term Actions (Next 1-2 Weeks)

1. [High priority drift items]
2. [Grouped related fixes]

### Medium-term Actions (This Sprint/Month)

1. [Medium priority items that can be batched]
2. [Documentation and testing improvements]

### Backlog Items (Future Sprints)

1. [Low priority optimizations]
2. [Nice-to-have improvements]

---

## Progress Tracking

### Drift Resolution

- [ ] **Week 1**: [Target number] critical items resolved
- [ ] **Week 2**: [Target number] high priority items resolved
- [ ] **Week 3-4**: [Target number] medium priority items resolved
- [ ] **Ongoing**: Low priority items as time permits

### Re-evaluation

- Next drift detection recommended: [Date]
- Trigger re-evaluation if:
  - Constitution is updated (version change)
  - Major feature additions
  - Security incident or audit
  - Quarterly review cycle

---

## Notes

### Drift Prevention

- Run `@sdd-drift` after major changes
- Include drift checks in PR templates
- Schedule regular constitutional reviews
- Update constitution as project evolves
- Document architectural decisions

### Common Patterns

- **Documentation drift**: README not updated with new features
- **Dependency drift**: Outdated or vulnerable packages
- **Test coverage drift**: New code without tests
- **Security drift**: Secrets accidentally committed
- **Standard drift**: Inconsistent code formatting

### Review Checklist

- [ ] All drift items have clear requirements references
- [ ] Effort estimates are realistic
- [ ] Remediation steps are actionable
- [ ] Priority levels are appropriate
- [ ] No duplicate drift items across principles
- [ ] Summary statistics are accurate
