<!--
Copyright (c) Github Speckit
MIT License
-->

# Tasks

Generate an actionable, dependency-ordered tasks.md for the feature based on available design artifacts.

## Usage

- `@sdd-tasks <feature-name>` - Generate tasks for specified feature

**Note**: The `<feature-name>` parameter is **REQUIRED**. If not provided, an error will be generated.

---

## Generate Tasks

The user **MUST** provide a feature name. This parameter is compulsory.

### Steps

1. **Validate feature name parameter**:

   - **If no feature name provided**: ERROR "Feature name is required. Usage: @sdd-tasks <feature-name>"
   - **If feature name provided**: Continue with the specified feature
   - Verify the feature exists in `.specify/specs/`
   - If feature doesn't exist: ERROR "Feature '<feature-name>' not found in specs/. Available features: [list directory names from .specify/specs/]"
   - Set FEATURE_NAME to the provided feature name

2. Run `.specify/scripts/bash/check-task-prerequisites.sh <feature-name> --json` from repo root and parse FEATURE_DIR and AVAILABLE_DOCS list. All paths must be absolute.

3. Load and analyze available design documents:

   - Always read plan.md for tech stack and libraries
   - IF EXISTS: Read data-model.md for entities
   - IF EXISTS: Read contracts/ for API endpoints
   - IF EXISTS: Read research.md for technical decisions
   - IF EXISTS: Read quickstart.md for test scenarios
   - **Check spec.md for Reference Folder**: If specified, load all files from the referenced folder in `.specify/reference/[folder-name]/` for additional context

4. **Validate Plan Completeness**:

   - Check if the plan.md file contains "NEED CLARIFICATION"
   - **If "NEED CLARIFICATION" is found**: STOP execution and ERROR with:

     ```
     ERROR: Cannot proceed with task generation - implementation plan requires clarification

     The implementation plan at <FEATURE_DIR>/plan.md contains unresolved "NEED CLARIFICATION" items.

     Please run @sdd-plan <feature-name> again with the required information to complete
     the implementation plan before generating tasks.
     ```

   - **If no "NEED CLARIFICATION" found**: Continue to next step

5. **Load Constitutional Standards (Context-Aware)**: Based on the tasks being generated, load relevant sections:

   **Task Type Detection**:

   - If tasks involve **testing** (test files, coverage): Load `testing,branching`
   - If tasks involve **API/endpoints** (contracts, services): Load `core,architecture,security,branching`
   - If tasks involve **infrastructure** (deployment, configs): Load `core,operations,security,branching`
   - If tasks are **mixed or general**: Load `core,testing,architecture,branching`

   **Execution**:

   ```bash
   # Determine task type from plan.md and available artifacts
   # Then load appropriate sections
   .specify/scripts/bash/load-constitution.sh "<section-list>"
   ```

   **Purpose**: Load only standards relevant to the tasks being created.

   **Note**: Not all projects have all documents. For example:

   - CLI tools might not have contracts/
   - Simple libraries might not need data-model.md
   - Generate tasks based on what's available

6. Generate tasks following the template:

   - Use `.specify/templates/tasks-template.md` as the base
   - **IMPORTANT**: All tasks must use checkbox format `- [ ] T001 Task description`
   - Replace example tasks with actual tasks based on:
     - **Setup tasks**: Project init, dependencies, linting
     - **Test tasks [P]**: One per contract, one per integration scenario
     - **Core tasks**: One per entity, service, CLI command, endpoint
     - **Integration tasks**: DB connections, middleware, logging
     - **Polish tasks [P]**: Unit tests, performance, docs

7. Task generation rules:

   - Each contract file → contract test task marked [P]
   - Each entity in data-model → model creation task marked [P]
   - Each endpoint → implementation task (not parallel if shared files)
   - Each user story → integration test marked [P]
   - Different files = can be parallel [P]
   - Same file = sequential (no [P])
   - **All tasks must start with `- [ ]` checkbox syntax**

8. Order tasks by dependencies:

   - Setup before everything
   - Tests before implementation (TDD)
   - Models before services
   - Services before endpoints
   - Core before integration
   - Everything before polish

9. Include parallel execution examples:

   - Group [P] tasks that can run together
   - Show actual Task agent commands

10. Create FEATURE_DIR/tasks.md with:

- Correct feature name from implementation plan
- Numbered tasks (T001, T002, etc.) with checkboxes `- [ ]`
- Clear file paths for each task
- Dependency notes
- Parallel execution guidance

The tasks.md should be immediately executable - each task must be specific enough that an LLM can complete it without additional context.
