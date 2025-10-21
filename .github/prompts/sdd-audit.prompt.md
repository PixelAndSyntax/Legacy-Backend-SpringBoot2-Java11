# Audit

Audit the implementation against the specification to validate quality and alignment.

## Usage

- `@sdd-audit <feature-name>` - Audit implementation for specified feature

**Note**: The `<feature-name>` parameter is **REQUIRED**. If not provided, an error will be generated.

---

## Audit Implementation

The user **MUST** provide a feature name. This parameter is compulsory.

### Steps

1. **Validate feature name parameter**:

   - **If no feature name provided**: ERROR "Feature name is required. Usage: @sdd-audit <feature-name>"
   - **If feature name provided**: Continue with the specified feature
   - Verify the feature exists in `.specify/specs/`
   - If feature doesn't exist: ERROR "Feature '<feature-name>' not found in specs/. Available features: [list directory names from .specify/specs/]"
   - Set FEATURE_NAME to the provided feature name
   - Set FEATURE_DIR to `specs/<feature-name>/`
   - Run `.specify/scripts/bash/check-prerequisites.sh <feature-name> --json --require-tasks` to validate and parse SPEC_FILE and AVAILABLE_DOCS. All paths must be absolute.

2. **Verify required files exist**:

   - spec.md (required)
   - plan.md (required)
   - tasks.md (required)
   - If any missing: ERROR "Cannot audit without complete specification, plan, and tasks"

3. **Load the audit template**:

   - Use `.specify/templates/audit-template.md` as the structure
   - Set Input to feature directory path
   - Run the Execution Flow (main) function steps 1-9
   - The template is self-contained and executable
   - Follow error handling and gate checks as specified

4. **Load and analyze specification documents**:

   - Read spec.md for requirements and acceptance criteria
   - Read plan.md for technical design and architecture
   - Read tasks.md for implementation expectations
   - **If Reference Context exists**: Use documented patterns and standards
   - Note all functional and non-functional requirements

5. **Load Constitutional Standards (Progressive - Phase 1)**: Start with critical sections for initial audit:

   **Phase 1 - Critical Compliance** (Always Load):

   ```bash
   .specify/scripts/bash/load-constitution.sh "core,testing,security,branching"
   ```

   **Sections Loaded**:

   - **core**: Technology stack, coding standards, error handling, validation
   - **testing**: Coverage requirements, test organization, security testing
   - **security**: Security principles, secrets management, input validation
   - **branching**: Git workflow, commit standards

   **Purpose**: Phase 1 covers most common compliance issues.

6. Audit the implementation (Phase 1 - Initial Assessment): - **Requirements Coverage**: Verify all functional requirements (FR-XXX) are implemented

   - **Acceptance Criteria**: Test against each acceptance scenario from spec.md
   - **Technical Alignment**: Compare implementation against plan.md architecture
   - **Task Completion**: Check all tasks in tasks.md are marked complete [X]
   - **Code Quality (Core Standards)**: Assess against loaded core standards:
     - Error handling patterns
     - Logging with correlation IDs
     - Input validation
     - Secret handling
   - **Testing (Testing Standards)**: Verify against loaded testing standards:
     - Test coverage thresholds
     - Test file organization
     - Security test presence
     - Mock usage patterns
   - **Security (Security Standards)**: Check against loaded security standards:
     - No plaintext secrets
     - Input sanitization
     - Authentication/authorization
     - Security logging
   - **Branching Compliance**: Verify branch names and commits follow standards

   **Calculate Phase 1 Quality Score**: Based on core, testing, security, and branching compliance.

7. **Progressive Loading (Phase 2) - If Issues Detected**: Load additional sections only if Phase 1 reveals specific issues:

   ```bash
   # Load architecture section if architectural issues detected
   if [[ $architecture_issues_found == true ]]; then
     .specify/scripts/bash/load-constitution.sh "architecture"
   fi

   # Load observability section if logging/monitoring issues detected
   if [[ $observability_issues_found == true ]]; then
     .specify/scripts/bash/load-constitution.sh "observability"
   fi

   # Load operations section if deployment/config issues detected
   if [[ $operations_issues_found == true ]]; then
     .specify/scripts/bash/load-constitution.sh "operations"
   fi
   ```

   **Phase 2 - Deep Dive Auditing**: Audit against additionally loaded sections:

   - **Architecture**: Service patterns, API design, database usage (if loaded)
   - **Observability**: Structured logging, metrics, tracing (if loaded)
   - **Operations**: Feature toggles, dependency management, versioning (if loaded)

8. **Progressive Loading (Phase 3) - If Score < 80%**: Load remaining sections for comprehensive review:

   ```bash
   if [[ $overall_score < 80 ]]; then
     .specify/scripts/bash/load-constitution.sh "optional"
   fi
   ```

   **Phase 3 - Comprehensive Audit**: Review documentation standards, glossary, enforcement policies.

9. Compare against specification expectations:

   - Identify missing functionality
   - Note incomplete tasks
   - Flag deviations from technical plan
   - Detect untested requirements
   - Find edge cases not handled
   - **Flag constitutional violations**: Document any deviations from loaded constitutional standards
   - Prioritize issues by severity:
     - **Critical**: Missing core functionality, security issues, broken acceptance criteria, constitutional violations
     - **High**: Incomplete requirements, significant deviations, missing tests
     - **Medium**: Code quality issues, minor deviations, documentation gaps
     - **Low**: Style inconsistencies, optimization opportunities

10. Calculate compliance metrics:

- Requirements coverage: (Implemented FRs / Total FRs) × 100
- Task completion: (Completed tasks / Total tasks) × 100
- Test coverage: (Tested scenarios / Total scenarios) × 100
- Constitutional compliance: (Passed standards / Total standards checked) × 100
- Overall quality score: Weighted average of all metrics
- **Phases Executed**: Report which audit phases were run (Phase 1, 2, 3)
- **Sections Loaded**: List all constitutional sections that were loaded

11. Create `FEATURE_DIR/AUDIT.md` following the template format:

- **IMPORTANT**: If `AUDIT.md` already exists in feature directory, overwrite it
- Use absolute path: `[repo-root]/specs/[feature-name]/AUDIT.md`
- Header with audit date, feature name, and quality scores
- **Audit Phases Section**: Document which phases were executed and why
- **Constitutional Compliance Section**: List loaded sections and compliance results
- Summary of compliance metrics
- Requirements coverage section
- Issues grouped by severity
- Each issue with:
  - Clear description with file paths
  - Reference to specification requirement or constitutional standard
  - Current state vs expected state
  - Priority level (Critical/High/Medium/Low)
  - Suggested remediation steps
- Recommendations for improvement
- Sign-off criteria for completion

12. Report summary with:

- Overall quality score and compliance percentage
- Audit phases executed (Phase 1/2/3) and token efficiency
- Constitutional sections loaded
- Total issues by priority
- Requirements coverage status
- Task completion status
- Test coverage status
- Constitutional compliance status
- Ready for production: Yes/No with reasoning
- Next actions required
- Confirm file written to feature directory AUDIT.md

```

```
