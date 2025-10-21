```markdown
Detect drift between the current project state and the constitution, creating a realignment TODO list.

1. Check if constitution files exist in `.specify/memory/constitution/`. If not, throw an error:
   "ERROR: Constitution not found. Run @sdd-init first to create the project constitution files."

2. Read the modular constitution files from `.specify/memory/constitution/` to understand all principles and requirements:

   - `core.md` - Technology stack, coding standards
   - `architecture.md` - Service patterns, design principles
   - `testing.md` - Test strategy, coverage requirements
   - `security.md` - Security policies
   - `observability.md` - Logging, monitoring standards
   - `optional.md` - Project-specific standards

3. Load the drift report template:

   - Use `.specify/templates/drift-template.md` as the structure
   - Set Input to current project state
   - Run the Execution Flow (main) function steps 1-8
   - The template is self-contained and executable
   - Follow error handling and gate checks as specified

4. Scan the current project structure and files to detect drift:

   - Read README.md, package.json, or similar project files
   - Check for required documentation (API docs, setup guides, etc.)
   - Examine code structure and organization
   - Review testing setup and coverage
   - **Security drift**: Check for exposed secrets, vulnerable dependencies, insecure configurations, missing authentication/authorization
   - **Coding standards drift**: Verify linting rules, formatting consistency, naming conventions, code complexity
   - Evaluate CI/CD and deployment practices
   - Assess code quality tools and standards

5. Compare project state against each constitutional principle to identify drift:

   - Identify gaps where the project doesn't meet requirements
   - Note missing files, configurations, or practices
   - Flag areas that need improvement or implementation
   - Prioritize items based on constitutional importance and security impact:
     - **Critical**: Security vulnerabilities, exposed secrets, unsafe dependencies
     - **High**: Missing security controls, coding standard violations, broken builds
     - **Medium**: Documentation gaps, incomplete testing, minor quality issues
     - **Low**: Style inconsistencies, optimization opportunities

6. Calculate drift score:

   - (Principles with drift / Total principles) × 100
   - Include severity weighting for impact assessment

7. Create `.specify/specs/CONSTITUTION_DRIFT.md` following the template format:

   - **IMPORTANT**: If `.specify/specs/CONSTITUTION_DRIFT.md` already exists, overwrite it with the new drift report
   - Use absolute path: `[repo-root]/.specify/specs/CONSTITUTION_DRIFT.md`
   - Header with drift detection date, constitution version, and drift score
   - Summary statistics section
   - One section per constitutional principle
   - Each drift item with detailed information:
     - Clear description with file paths
     - Reference to specific constitutional requirement
     - Current state vs required state
     - Priority level (Critical/High/Medium/Low)
     - Estimated effort or complexity
     - Suggested remediation steps
   - Action plan with timeline
   - Progress tracking section

8. Report summary with:
   - Total drift items by priority
   - Overall compliance percentage and drift score
   - Compliant vs non-compliant principles
   - Next recommended actions
   - Suggested timeline for realignment
   - Confirm file written to `.specify/specs/CONSTITUTION_DRIFT.md`
```
