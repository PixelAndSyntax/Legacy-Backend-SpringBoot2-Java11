# Tasks

Generate an actionable, dependency-ordered tasks.md for the feature based on available design artifacts.

## Usage

- `@tasks` - Generate tasks for current/auto-detected feature
- `@tasks <feature-name>` - Generate tasks for specified feature

---

## Generate Tasks

The user may optionally provide a feature name. If not provided, the system will auto-detect.

### Steps

1. **Determine which feature to generate tasks for**:

   - **If user provided feature name**: Use that specific feature
   - **If no feature name provided**:
     - List all available features in `specs/`
     - If only one feature exists: use it automatically
     - If multiple features exist: ERROR "Multiple specs found. Please specify which feature to generate tasks for: @tasks <feature-name>"
     - Available features: [list directory names from specs/]
   - Set FEATURE_NAME to the determined feature name

2. Run `.specify/scripts/bash/check-task-prerequisites.sh <feature-name> --json` from repo root and parse FEATURE_DIR and AVAILABLE_DOCS list. All paths must be absolute.

3. Load and analyze available design documents:

   - Always read plan.md for tech stack and libraries
   - IF EXISTS: Read data-model.md for entities
   - IF EXISTS: Read contracts/ for API endpoints
   - IF EXISTS: Read research.md for technical decisions
   - IF EXISTS: Read quickstart.md for test scenarios
   - **Check spec.md for Reference Folder**: If specified, load all files from the referenced folder in `.specify/reference/[folder-name]/` for additional context

4. **Load Constitutional Standards (Context-Aware)**: Based on the tasks being generated, load relevant sections:

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

5. Generate tasks following the template:

   - Use `.specify/templates/tasks-template.md` as the base
   - **IMPORTANT**: All tasks must use checkbox format `- [ ] T001 Task description`
   - Replace example tasks with actual tasks based on:
     - **Setup tasks**: Project init, dependencies, linting
     - **Test tasks [P]**: One per contract, one per integration scenario
     - **Core tasks**: One per entity, service, CLI command, endpoint
     - **Integration tasks**: DB connections, middleware, logging
     - **Polish tasks [P]**: Unit tests, performance, docs

6. Task generation rules:

   - Each contract file → contract test task marked [P]
   - Each entity in data-model → model creation task marked [P]
   - Each endpoint → implementation task (not parallel if shared files)
   - Each user story → integration test marked [P]
   - Different files = can be parallel [P]
   - Same file = sequential (no [P])
   - **All tasks must start with `- [ ]` checkbox syntax**

7. Order tasks by dependencies:

   - Setup before everything
   - Tests before implementation (TDD)
   - Models before services
   - Services before endpoints
   - Core before integration
   - Everything before polish

8. Include parallel execution examples:

   - Group [P] tasks that can run together
   - Show actual Task agent commands

9. Create FEATURE_DIR/tasks.md with:
   - Correct feature name from implementation plan
   - Numbered tasks (T001, T002, etc.) with checkboxes `- [ ]`
   - Clear file paths for each task
   - Dependency notes
   - Parallel execution guidance

The tasks.md should be immediately executable - each task must be specific enough that an LLM can complete it without additional context.
