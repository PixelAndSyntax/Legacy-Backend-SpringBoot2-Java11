<!--
Copyright (c) Github Speckit
MIT License
-->

# Implement

Execute the implementation plan by progressively executing all tasks defined in tasks.md

## Usage

- `@sdd-implement <feature-name>` - Execute implementation for specified feature

**Note**: The `<feature-name>` parameter is **REQUIRED**. If not provided, an error will be generated.

---

## Execute Implementation

The user **MUST** provide a feature name. This parameter is compulsory.

### Steps

1. **Validate feature name parameter**:

   - **If no feature name provided**: ERROR "Feature name is required. Usage: @sdd-implement <feature-name>"
   - **If feature name provided**: Continue with the specified feature
   - Verify the feature exists in `.specify/specs/`
   - If feature doesn't exist: ERROR "Feature '<feature-name>' not found in specs/. Available features: [list directory names from .specify/specs/]"
   - Set FEATURE_NAME to the provided feature name

2. Run `.specify/scripts/bash/check-implementation-prerequisites.sh <feature-name> --json` from repo root and parse FEATURE_DIR and AVAILABLE_DOCS list. All paths must be absolute.

3. Load and analyze the implementation context:

   - **REQUIRED**: Read tasks.md for the complete task list and execution plan
   - **REQUIRED**: Read plan.md for tech stack, architecture, and file structure
   - **IF EXISTS**: Read data-model.md for entities and relationships
   - **IF EXISTS**: Read contracts/ for API specifications and test requirements
   - **IF EXISTS**: Read research.md for technical decisions and constraints
   - **IF EXISTS**: Read quickstart.md for integration scenarios

4. **Load Constitutional Standards (Just-In-Time)**: Analyze the task being implemented and load only relevant sections:

   **File Type Detection**:

   - If implementing **test files** (_.test._, _.spec._): Load `testing,branching`
   - If implementing **service/business logic** (services/, handlers/): Load `core,architecture,observability,branching`
   - If implementing **auth/security** (auth*, security*, validation\*): Load `core,security,branching`
   - If implementing **database/models** (models/, entities/, repositories/): Load `core,architecture,branching`
   - If implementing **API/routes** (routes/, controllers/, endpoints/): Load `core,architecture,security,branching`
   - If implementing **config/deployment** (_.yml, _.yaml, Dockerfile): Load `operations,security,branching`
   - If implementing **logging/monitoring** (logger*, monitor*, metrics\*): Load `observability,branching`

   **Execution**:

   ```bash
   # Detect file type from current task
   # Then load appropriate sections
   .specify/scripts/bash/load-constitution.sh "<section-list>"
   ```

   **Purpose**: Load only standards needed for the current implementation task.

   **Re-load on Task Change**: When moving to a different task category, reload appropriate sections.

5. Parse tasks.md structure and extract:

   - **Task phases**: Setup, Tests, Core, Integration, Polish
   - **Task dependencies**: Sequential vs parallel execution rules
   - **Task details**: ID, description, file paths, parallel markers [P]
   - **Execution flow**: Order and dependency requirements

6. Execute implementation following the task plan:

   - **Phase-by-phase execution**: Complete each phase before moving to the next
   - **Respect dependencies**: Run sequential tasks in order, parallel tasks [P] can run together
   - **Follow TDD approach**: Execute test tasks before their corresponding implementation tasks
   - **File-based coordination**: Tasks affecting the same files must run sequentially
   - **Validation checkpoints**: Verify each phase completion before proceeding
   - **Apply Constitutional Standards**: For each implementation, follow:
     - **Coding standards**: Error handling, logging patterns, validation (from core)
     - **Architectural patterns**: Service structure, API design (from architecture)
     - **Testing requirements**: Test organization, coverage, mocking (from testing)
     - **Security practices**: Input validation, secrets management, auth (from security)
     - **Observability**: Structured logging, correlation IDs, metrics (from observability)

7. Implementation execution rules:

   - **Setup first**: Initialize project structure, dependencies, configuration
   - **Tests before code**: If you need to write tests for contracts, entities, and integration scenarios
   - **Core development**: Implement models, services, CLI commands, endpoints
   - **Integration work**: Database connections, middleware, logging, external services
   - **Polish and validation**: Unit tests, performance optimization, documentation

8. Progress tracking and error handling:

   - Report progress after each completed task
   - Halt execution if any non-parallel task fails
   - For parallel tasks [P], continue with successful tasks, report failed ones
   - Provide clear error messages with context for debugging
   - Suggest next steps if implementation cannot proceed
   - **IMPORTANT** For completed tasks, make sure to mark the task off as [X] in the tasks file.

9. Completion validation:
   - Verify all required tasks are completed
   - Check that implemented features match the original specification
   - Validate that tests pass and coverage meets requirements
   - Confirm the implementation follows the technical plan
   - Report final status with summary of completed work

Note: This command assumes a complete task breakdown exists in sdd-tasks.md. If tasks are incomplete or missing, suggest running the sdd-tasks prompt first to regenerate the task list.
