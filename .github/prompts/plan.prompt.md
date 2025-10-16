# Plan

Create an imp - List all available features in `specs/`

- If none: ERROR "No features found. Use @specify to create a feature first"
- If multiple:
  - Available features: [list directory names from specs/]ntation plan for a feature specification using the plan template to generate design artifacts.

## Usage

- `@plan` - Create implementation plan for current/auto-detected feature
- `@plan <feature-name>` - Create implementation plan for specified feature

---

## Create Implementation Plan

The user may optionally provide a feature name. If not provided, the system will auto-detect.

### Steps

1. **Determine which feature to plan**:

   - **If user provided feature name**: Use that specific feature
   - **If no feature name provided**:
     - List all available features in `.specify/specs/`
     - If only one feature exists: use it automatically
     - If multiple features exist: ERROR "Multiple specs found. Please specify which feature to plan: @plan <feature-name>"
     - Available features: [list directory names from .specify/specs/]
   - Set FEATURE_NAME to the determined feature name

2. Run `.specify/scripts/bash/setup-plan.sh <feature-name> --json` from the repo root and parse JSON for FEATURE_SPEC, IMPL_PLAN, SPECS_DIR, BRANCH. All future file paths must be absolute.

3. Read and analyze the feature specification to understand:

   - The feature requirements and user stories
   - Functional and non-functional requirements
   - Success criteria and acceptance criteria
   - Any technical constraints or dependencies mentioned
   - **If Reference Folder is specified**: Load all files from the referenced folder in `.specify/reference/[folder-name]/` for additional context

4. **Load Constitutional Standards**: Execute `.specify/scripts/bash/load-constitution.sh` with targeted sections for planning:

   **Planning-Specific Sections**:

   ```bash
   .specify/scripts/bash/load-constitution.sh "core,architecture,testing,branching"
   ```

   **Purpose**: Load only the sections needed for technical planning:

   - **core**: Technology stack, coding standards, linting requirements
   - **architecture**: Architectural principles, design patterns, API design
   - **testing**: Testing strategies, coverage requirements, test organization
   - **branching**: Git workflow and commit standards

   **Note**: This is ~60% more efficient than loading the full constitution while retaining all planning-relevant standards.

5. Execute the implementation plan template:

   - Load `.specify/templates/plan-template.md` (already copied to IMPL_PLAN path)
   - Set Input path to FEATURE_SPEC
   - Run the Execution Flow (main) function steps 1-9
   - The template is self-contained and executable
   - Follow error handling and gate checks as specified
   - Let the template guide artifact generation in $SPECS_DIR:
     - Phase 0 generates research.md
     - Phase 1 generates data-model.md, contracts/, quickstart.md
     - Phase 2 generates tasks.md
   - Incorporate user-provided details from arguments into Technical Context
   - Update Progress Tracking as you complete each phase
   - **IMPORTANT**: Always include checkboxes `- [ ]` for all progress tracking items, phases, and gate checks

6. Verify execution completed:

   - Check Progress Tracking shows all phases complete
   - Ensure all required artifacts were generated
   - Confirm no ERROR states in execution

7. Report results with feature name, file paths, and generated artifacts.

Use absolute paths with the repository root for all file operations to avoid path issues.
