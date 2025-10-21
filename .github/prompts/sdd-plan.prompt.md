<!--
Copyright (c) Github Speckit
MIT License
-->

# Plan

Create an implementation plan for a feature specification using the plan template to generate design artifacts.

## Usage

- `@sdd-plan <feature-name>` - Create implementation plan for specified feature

**Note**: The `<feature-name>` parameter is **REQUIRED**. If not provided, an error will be generated.

---

## Create Implementation Plan

The user **MUST** provide a feature name. This parameter is compulsory.

### Steps

1. **Validate feature name parameter**:

   - **If no feature name provided**: ERROR "Feature name is required. Usage: @sdd-plan <feature-name>"
   - **If feature name provided**: Continue with the specified feature
   - Verify the feature exists in `.specify/specs/`
   - If feature doesn't exist: ERROR "Feature '<feature-name>' not found in specs/. Available features: [list directory names from .specify/specs/]"
   - Set FEATURE_NAME to the provided feature name

2. Run `.specify/scripts/bash/setup-plan.sh <feature-name> --json` from the repo root and parse JSON for FEATURE_SPEC, IMPL_PLAN, SPECS_DIR, BRANCH. All future file paths must be absolute.

3. Read and analyze the feature specification to understand:

   - The feature requirements and user stories
   - Functional and non-functional requirements
   - Success criteria and acceptance criteria
   - Any technical constraints or dependencies mentioned
   - **If Reference Folder is specified**: Load all files from the referenced folder in `.specify/reference/[folder-name]/` for additional context

4. **Validate Specification Completeness**:

   - Check if the specification file (FEATURE_SPEC) contains "NEED CLARIFICATION"
   - **If "NEED CLARIFICATION" is found**: STOP execution and ERROR with:

     ```
     ERROR: Cannot proceed with planning - specification requires clarification

     The feature specification at <FEATURE_SPEC> contains unresolved "NEED CLARIFICATION" items.

     Please run @sdd-specify <feature-name> again with the required information to complete
     the specification before creating an implementation plan.
     ```

   - **If no "NEED CLARIFICATION" found**: Continue to next step

5. **Load Constitutional Standards**: Execute `.specify/scripts/bash/load-constitution.sh` with targeted sections for planning:

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

6. Execute the implementation plan template:

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

7. Verify execution completed:

   - Check Progress Tracking shows all phases complete
   - Ensure all required artifacts were generated
   - Confirm no ERROR states in execution

8. Report results with feature name, file paths, and generated artifacts.

Use absolute paths with the repository root for all file operations to avoid path issues.
