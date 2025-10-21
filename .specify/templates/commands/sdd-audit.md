```markdown
Audit the implementation against the specification to validate quality and alignment.

1. Run `.specify/scripts/bash/check-prerequisites.sh --json --require-tasks` from repo root and parse FEATURE_DIR, SPEC_FILE, and AVAILABLE_DOCS. All paths must be absolute.

2. Verify required files exist:

   - spec.md (required)
   - plan.md (required)
   - tasks.md (required)
   - If any missing: ERROR "Cannot audit without complete specification, plan, and tasks"

3. Load the audit template:

   - Use `.specify/templates/audit-template.md` as the structure
   - Set Input to feature directory path
   - Run the Execution Flow (main) function steps 1-9
   - The template is self-contained and executable
   - Follow error handling and gate checks as specified

4. Load and analyze specification documents:

   - Read spec.md for requirements and acceptance criteria
   - Read plan.md for technical design and architecture
   - Read tasks.md for implementation expectations
   - **If Reference Context exists**: Use documented patterns and standards
   - Note all functional and non-functional requirements

5. Audit the implementation:

   - **Requirements Coverage**: Verify all functional requirements (FR-XXX) are implemented
   - **Acceptance Criteria**: Test against each acceptance scenario from spec.md
   - **Technical Alignment**: Compare implementation against plan.md architecture
   - **Task Completion**: Check all tasks in tasks.md are marked complete [X]
   - **Code Quality**: Assess code organization, naming, documentation
   - **Testing**: Verify test coverage matches requirements
   - **Error Handling**: Check edge cases and error scenarios are handled
   - **Performance**: Validate meets any specified performance criteria
   - **Security**: Check for security issues, exposed secrets, vulnerabilities

6. Compare against specification expectations:

   - Identify missing functionality
   - Note incomplete tasks
   - Flag deviations from technical plan
   - Detect untested requirements
   - Find edge cases not handled
   - Prioritize issues by severity:
     - **Critical**: Missing core functionality, security issues, broken acceptance criteria
     - **High**: Incomplete requirements, significant deviations, missing tests
     - **Medium**: Code quality issues, minor deviations, documentation gaps
     - **Low**: Style inconsistencies, optimization opportunities

7. Calculate compliance metrics:

   - Requirements coverage: (Implemented FRs / Total FRs) × 100
   - Task completion: (Completed tasks / Total tasks) × 100
   - Test coverage: (Tested scenarios / Total scenarios) × 100
   - Overall quality score: Weighted average of all metrics

8. Create `FEATURE_DIR/AUDIT.md` following the template format:

   - **IMPORTANT**: If `AUDIT.md` already exists in feature directory, overwrite it
   - Use absolute path: `[repo-root]/.specify/specs/[feature-name]/AUDIT.md`
   - Header with audit date, feature name, and quality scores
   - Summary of compliance metrics
   - Requirements coverage section
   - Issues grouped by severity
   - Each issue with:
     - Clear description with file paths
     - Reference to specification requirement
     - Current state vs expected state
     - Priority level (Critical/High/Medium/Low)
     - Suggested remediation steps
   - Recommendations for improvement
   - Sign-off criteria for completion

9. Report summary with:
   - Overall quality score and compliance percentage
   - Total issues by priority
   - Requirements coverage status
   - Task completion status
   - Test coverage status
   - Ready for production: Yes/No with reasoning
   - Next actions required
   - Confirm file written to feature directory AUDIT.md
```
