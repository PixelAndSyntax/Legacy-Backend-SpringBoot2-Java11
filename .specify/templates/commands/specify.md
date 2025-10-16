# Specify

Create or update the feature specification from a natural language feature description.

## Usage

- `@specify <feature_description>` - Create a new feature specification
- `@specify <feature_description> -ref <reference_folder>` - Create specification with additional context from a reference folder

---

## Create Feature Specification

The user will provide a feature description and optionally a reference folder for additional context.

### Validation Requirements

**CRITICAL**: All feature descriptions MUST include a valid branch type prefix. If the user provides a description without a proper prefix, immediately stop processing and return an error message.

### Steps

0. **Validate Branch Type Prefix**: Before proceeding, ensure the feature description includes a valid branch type prefix according to the modular constitution branching standards:

   - **REQUIRED**: The description MUST start with one of: `feat/`, `fix/`, `chore/`, `refactor/`, `test/`, `docs/`, `hotfix/`, `maintenance/`
   - **STOP AND ERROR**: If no valid prefix is provided, immediately stop processing and inform the user they must specify a branch type prefix
   - **Constitution Reference**: Per `.specify/memory/git-workflow.md` "Branching and Repository Standards", all branches must follow the `type/short-description` pattern

   **Valid Examples**:

   - ✅ `feat/user-authentication-system`
   - ✅ `fix/payment-timeout-issue`
   - ✅ `docs/api-documentation-update`
   - ❌ `user-authentication-system` (missing type prefix)
   - ❌ `new-feature/authentication` (invalid type)

   **ERROR HANDLING**: If the user's description does not start with a valid type prefix, respond with:

   ```
   ERROR: Invalid branch type prefix provided.

   The feature description must start with a valid branch type according to the constitution.md Branch Naming standards.

   Required format: type/description
   Valid types: feat, fix, chore, refactor, test, docs, hotfix, maintenance

   Examples:
   - feat/add-user-authentication
   - fix/resolve-payment-timeout
   - docs/update-api-documentation

   Please provide your feature description with a proper type prefix.
   ```

   Do not proceed with any further steps.

0.1. **Summarize the feature description**: After validating the prefix, create a concise summary of the feature description that is 80 characters or less. This summary should capture the essential meaning while preserving the required type prefix and being suitable for use as a git branch name. Preserve key technical terms and maintain clarity.

1. Run the script `.specify/scripts/bash/create-new-feature.sh --json "<summarized_description_with_prefix>"` from repo root and parse its JSON output for BRANCH_NAME and SPEC_FILE. All file paths must be absolute.
   **IMPORTANT** You must only ever run this script once. The JSON is provided in the terminal as output - always refer to it to get the actual content you're looking for.
   **NOTE** Branch names are automatically generated from the summarized description (max 65 chars after transformation).

   **Branch Name Generation**: The script automatically generates a git branch name from the summarized feature description by:

   - Converting the description to lowercase
   - Replacing all non-alphanumeric characters with hyphens
   - Removing consecutive hyphens
   - Trimming leading and trailing hyphens
   - Truncating to a maximum of 65 characters
   - Example: "High-Value Field Redaction & Structured Context" → "high-value-field-redaction---structured-context"

1.5. **Load Reference Folder (if provided)**: If the user specified a reference folder with `-ref <folder_name>`, check for `.specify/reference/<folder_name>/` and load all files in the folder. Extract and summarize:

- **Architecture & Patterns**: Code patterns, design decisions, and conventions found
- **Code Examples & Interfaces**: API signatures, interfaces, or code patterns to follow
- **Configuration & Setup**: Configuration patterns, environment setup, dependencies
- **Testing Approaches**: Testing patterns, fixtures, or utilities available
- **Key Technical Decisions**: Important technical choices and rationale

Store this as REFERENCE_CONTEXT for inclusion in the spec.

2. Load `.specify/templates/spec-template.md` to understand required sections.

3. Write the specification to SPEC_FILE using the template structure, replacing placeholders with concrete details derived from the feature description and reference folder (if provided) while preserving section order and headings.

   **If a reference folder was used**, include both:

   a) **YAML frontmatter at the very top**:

   ```yaml
   ---
   reference: <folder_name>
   ---
   ```

   b) **Reference Context section after User Stories**:

   ```markdown
   ## Reference Context

   **Reference Folder**: [folder-name]
   **Purpose**: Context from existing implementation for consistency and pattern reuse

   ### Key Insights from Reference Material

   #### Architecture & Patterns

   [Summarize architectural patterns, design decisions, and conventions found]

   #### Code Examples & Interfaces

   [List relevant API signatures, interfaces, or code patterns to follow]

   #### Configuration & Setup

   [Note any configuration patterns, environment setup, or dependencies]

   #### Testing Approaches

   [Document testing patterns, fixtures, or utilities available]

   ### Referenced Files

   [List files that were loaded and analyzed]
   ```

   This allows @plan and @tasks to use the pre-analyzed insights without re-loading files.

4. Report completion with branch name, spec file path, reference folder used (if any), and readiness for the next phase.

Note: The script creates the feature directory and initializes the spec file.

---

## Creating Reference Folders

Reference folders provide reusable context that enhances @specify, @plan, and @tasks workflows.

### How to Create a Reference Folder

1. **Summarize folder name**: If the description is long, create a concise summary (80 characters or less) in kebab-case format.

2. **Check if folder exists**: First check if `.specify/reference/<folder_name>/` already exists

3. **Create folder structure**: If it doesn't exist, create the folder `.specify/reference/<folder_name>/`

4. **Create README.md**: Create a `README.md` file in the new folder using the template below

5. **Confirm creation**: Let the user know the reference folder has been created and they can now edit it

### Template for README.md

```markdown
# [Feature Name] Requirements

## Primary User Story

As a [user type], I want [goal] so that [benefit].

## Acceptance Criteria

- [ ] Must have: [critical requirement]
- [ ] Should have: [important requirement]
- [ ] Could have: [nice-to-have requirement]

## User Scenarios

### Happy Path

1. User does X
2. System responds with Y
3. User sees Z

### Edge Cases

- What happens when [edge case 1]
- How to handle [edge case 2]
- Behavior for [edge case 3]

## Functional Requirements

### Core Features

- Feature 1: [description]
- Feature 2: [description]

### Business Rules

- Rule 1: [constraint/validation]
- Rule 2: [business logic]

## Key Entities

### Data Models

- **Entity1**: fields, relationships, constraints
- **Entity2**: fields, relationships, constraints

### APIs/Interfaces

- Endpoint patterns
- Expected inputs/outputs
- Error handling

## Technical Constraints

- Performance: [requirements]
- Security: [requirements]
- Compatibility: [requirements]

## Success Metrics

- How to measure success
- Key performance indicators
- User experience goals
```

### Using Reference Folders

After creating a reference folder:

1. Edit the README.md with specific requirements for that domain/feature area
2. Add additional files to the folder as needed
3. Use the folder when creating specifications:
   - `@specify <feature_description> -ref <folder_name>` - Creates spec with reference context
   - The reference folder name is stored in the spec's YAML frontmatter
   - `@plan` and `@tasks` automatically source the reference from the spec file
   - No need to pass `-ref` to @plan or @tasks - they read it from the spec

### Important Notes

- Only create the folder if it doesn't already exist
- The folder name should be descriptive and kebab-case (e.g., `user-authentication`, `payment-system`)
- The README.md serves as the primary reference document but additional files can be added
- Reference folders provide consistent context: define once in @specify, automatically used by @plan and @tasks
