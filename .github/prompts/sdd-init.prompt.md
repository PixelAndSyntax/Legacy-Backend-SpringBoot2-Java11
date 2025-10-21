<!--
Copyright (c) Github Speckit
Modified SDD-Toolkit
MIT License
-->

Create or update the project constitution from interactive or provided principle inputs, ensuring all dependent templates stay in sync.

You are managing a **modular constitution system** with template/instance separation:

**Templates** (`.specify/templates/constitution/`):

- `core-template.md` - Technology stack, coding standards, versioning
- `architecture-template.md` - Service patterns, design principles
- `testing-template.md` - Test strategy, coverage requirements
- `security-template.md` - Security policies, authentication
- `observability-template.md` - Logging, monitoring, metrics
- `optional-template.md` - Additional project-specific standards

**Working Copies** (`.specify/memory/constitution/`):

- `core.md`, `architecture.md`, `testing.md`, `security.md`, `observability.md`, `optional.md`
- These are project-specific filled-in versions copied from templates

Each template file contains placeholder tokens in square brackets (e.g. `[PROJECT_NAME]`, `[PRINCIPLE_1_NAME]`). Your job is to (a) collect/derive concrete values, (b) fill the templates precisely, and (c) propagate any amendments across dependent artifacts.

Follow this execution flow:

1. **Initialize constitution working copies if needed**:

   - Check if `.specify/memory/constitution/` directory exists
   - Check if `.specify/memory/constitution/core.md` exists

   - If **core.md does NOT exist in memory/constitution/**:

     - Inform the user: "Initializing modular constitution from templates..."
     - Create `.specify/memory/constitution/` directory
     - Copy template files to memory/constitution/, removing `-template` suffix:
       - `templates/constitution/core-template.md` → `memory/constitution/core.md`
       - `templates/constitution/architecture-template.md` → `memory/constitution/architecture.md`
       - `templates/constitution/testing-template.md` → `memory/constitution/testing.md`
       - `templates/constitution/security-template.md` → `memory/constitution/security.md`
       - `templates/constitution/observability-template.md` → `memory/constitution/observability.md`
       - `templates/constitution/optional-template.md` → `memory/constitution/optional.md`
     - Check if `.specify/memory/git-workflow.md` exists
       - If it **does NOT exist**: Copy `.specify/templates/git-workflow-template.md` to `.specify/memory/git-workflow.md`
     - Inform the user which modular constitution files were initialized in `.specify/memory/constitution/`
     - Proceed to next step

   - If **files exist in memory/constitution/**:

     - Load the existing constitution files from `.specify/memory/constitution/` that the user wants to update
     - Check if `.specify/memory/git-workflow.md` exists
       - If it **does NOT exist**: Copy `.specify/templates/git-workflow-template.md` to `.specify/memory/git-workflow.md`
     - Proceed to next step

   - Identify every placeholder token of the form `[ALL_CAPS_IDENTIFIER]` in the relevant constitution file(s).
     **IMPORTANT**: The user might require less or more principles than the ones used in the template. If a number is specified, respect that - follow the general template. You will update the doc accordingly.

2. Collect/derive values for placeholders:

   - Ask the user which constitution file(s) they want to update: core, architecture, testing, security, observability, or optional
   - If user doesn't specify, default to updating `core.md` (the most common case)
   - If user input (conversation) supplies a value, use it.
   - Otherwise infer from existing repo context (README, docs, prior constitution versions if embedded).
   - For governance dates: `RATIFICATION_DATE` is the original adoption date (if unknown ask or mark TODO), `LAST_AMENDED_DATE` is today if changes are made, otherwise keep previous.
   - Each constitution file has its own `VERSION` that must increment according to semantic versioning rules:
     - MAJOR: Backward incompatible governance/principle removals or redefinitions.
     - MINOR: New principle/section added or materially expanded guidance.
     - PATCH: Clarifications, wording, typo fixes, non-semantic refinements.
   - If version bump type ambiguous, propose reasoning before finalizing.

3. Draft the updated constitution content for the target file(s):

   - Replace every placeholder with concrete text (no bracketed tokens left except intentionally retained template slots that the project has chosen not to define yet—explicitly justify any left).
   - Preserve heading hierarchy and comments can be removed once replaced unless they still add clarifying guidance.
   - For **core.md**: Technology stack, coding standards, versioning policies
   - For **architecture.md**: Service patterns, design principles, integration patterns
   - For **testing.md**: Test strategy, coverage requirements, TDD practices
   - For **security.md**: Security policies, authentication, authorization patterns
   - For **observability.md**: Logging standards, monitoring, metrics requirements
   - For **optional.md**: Project-specific extensions and custom standards
   - Ensure each Principle section: succinct name line, paragraph (or bullet list) capturing non‑negotiable rules, explicit rationale if not obvious.
   - Ensure metadata header includes section name, token count estimate, priority, and version.

4. Consistency propagation checklist (convert prior checklist into active validations):

   - Read `.specify/templates/plan-template.md` and ensure any "Constitution Check" or rules align with updated principles.
   - Read `.specify/templates/spec-template.md` for scope/requirements alignment—update if constitution adds/removes mandatory sections or constraints.
   - Read `.specify/templates/tasks-template.md` and ensure task categorization reflects new or removed principle-driven task types (e.g., observability, versioning, testing discipline).
   - Read each command file in `.specify/templates/commands/*.md` to verify no outdated references remain when generic guidance is required.
   - Read any runtime guidance docs (e.g., `README.md`). Update references to principles changed.
   - **Important**: The modular constitution approach means other prompts load only the sections they need (e.g., `@sdd-implement` loads `testing,branching` for test files, `core,architecture,security,branching` for API files). Ensure your changes maintain this modularity.

5. Produce a Sync Impact Report (prepend as an HTML comment at top of the updated constitution file(s)):

   - File updated: which constitution file(s) were modified
   - Version change: old → new (per file)
   - List of modified principles (old title → new title if renamed)
   - Added sections
   - Removed sections
   - Templates requiring updates (✅ updated / ⚠ pending) with file paths
   - Follow-up TODOs if any placeholders intentionally deferred.
   - Impact on other constitution files (e.g., if core.md changes affect architecture.md)

6. Validation before final output:

   - No remaining unexplained bracket tokens.
   - Version line matches report.
   - Dates ISO format YYYY-MM-DD.
   - Principles are declarative, testable, and free of vague language ("should" → replace with MUST/SHOULD rationale where appropriate).

7. Write the completed constitution back to `.specify/memory/constitution/<file>.md` (overwrite the specific file(s) being updated in the memory folder).

8. Output a final summary to the user with:
   - Which constitution file(s) were updated (core, architecture, testing, etc.)
   - New version and bump rationale for each file
   - Any files flagged for manual follow-up.
   - Suggested commit message (e.g., `docs: update constitution/core.md to v2.1.0 (add TypeScript strict mode requirement)`).
   - Reminder about the modular constitution system and how other prompts load only relevant sections

Formatting & Style Requirements:

- Use Markdown headings exactly as in the template (do not demote/promote levels).
- Wrap long rationale lines to keep readability (<100 chars ideally) but do not hard enforce with awkward breaks.
- Keep a single blank line between sections.
- Avoid trailing whitespace.

If the user supplies partial updates (e.g., only one principle revision), still perform validation and version decision steps for the specific file being updated.

If critical info missing (e.g., ratification date truly unknown), insert `TODO(<FIELD_NAME>): explanation` and include in the Sync Impact Report under deferred items.

Do not create a new template; always operate on the existing `.specify/memory/constitution/<file>.md` files (working copies).

**Template/Instance Architecture**:

- **Templates** (`templates/constitution/*-template.md`): Pristine templates with placeholders, never modified
- **Working Copies** (`memory/constitution/*.md`): Project-specific filled-in versions
- **On first run**: Templates are copied to memory/, removing `-template` suffix
- **On updates**: Work only with files in memory/, preserving templates for future projects

**Modular Constitution Benefits**:

- **Token efficiency**: Load only the sections needed (e.g., `@sdd-implement` loads `testing,branching` for test files, not the entire constitution)
- **Focused updates**: Change architecture standards without touching testing standards
- **Clear ownership**: Each file has a specific purpose and version
- **Better scalability**: Add new constitution modules (e.g., `deployment.md`) without affecting existing ones
