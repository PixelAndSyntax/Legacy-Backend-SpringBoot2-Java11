<!--
Copyright (c) Github Speckit
MIT License
-->

# Specify

Create or update the feature specification from a natural language feature description.

## Usage

- `@sdd-specify <feature_description>` - Create a new feature specification (defaults to `feat` type)
- `@sdd-specify <feature_description> -type <branch_type>` - Create specification with specific branch type
- `@sdd-specify <feature_description> -ref <reference_folder>` - Create specification with reference context
- `@sdd-specify <feature_description> -type <branch_type> -ref <reference_folder>` - Create specification with both type and reference

### Arguments

- **`<feature_description>`** (REQUIRED): Natural language description of the feature without type prefix (first text after `@sdd-specify`)
- **`-type <branch_type>`** (OPTIONAL): Git branch type - must be one of: `feat`, `fix`, `chore`, `refactor`, `test`, `docs`, `hotfix`, `maintenance` (defaults to `feat`)
- **`-ref <reference_folder>`** (OPTIONAL): Name of the reference folder in `.specify/reference/` to use for context

### Examples

```bash
# Basic usage with default feat type
@sdd-specify user authentication system

# With explicit branch type:
@sdd-specify payment timeout issue -type fix

# With reference context:
@sdd-specify user authentication system -ref auth-patterns

# With both type and reference:
@sdd-specify api documentation update -type docs -ref api-standards
```

---

## Create Feature Specification

The user will provide a feature description (first text after the command), optionally a branch type via `-type` (defaults to `feat`), and optionally a reference folder via `-ref` for additional context.

### Validation Requirements

**CRITICAL**: Validate that required arguments are provided and that the branch type is valid.

### Validation Requirements

**CRITICAL**: Validate that required arguments are provided and that the branch type is valid.

### Steps

0. **Parse and Validate Arguments**: Before proceeding, parse the user input and validate:

   **Feature Description Extraction**:

   - **REQUIRED**: Extract the feature description from the first text after `@sdd-specify` (before any flags)
   - All text before the first `-` flag (if any) is the feature description
   - **STOP AND ERROR**: If no feature description is provided, immediately stop processing and return an error message

   **Branch Type Validation**:

   - **Default**: If `-type` is not provided, default to `feat`
   - **REQUIRED**: If `-type` is provided, it MUST be one of: `feat`, `fix`, `chore`, `refactor`, `test`, `docs`, `hotfix`, `maintenance`
   - **STOP AND ERROR**: If an invalid type is provided, immediately stop processing and inform the user of valid types

   **ERROR HANDLING**:

   If feature description is missing:

   ```
   ERROR: Missing required feature description

   The feature description is required.

   Usage:
   @sdd-specify <description>
   @sdd-specify <description> -type <type>
   @sdd-specify <description> -ref <reference_folder>

   Examples:
   @sdd-specify user authentication system
   @sdd-specify payment timeout issue -type fix
   @sdd-specify user authentication system -ref auth-patterns
   ```

   If `-type` has an invalid value:

   ```
   ERROR: Invalid branch type provided

   Branch type must be one of: feat, fix, chore, refactor, test, docs, hotfix, maintenance
   Default: feat (if -type is not specified)

   Valid examples:
   - @sdd-specify user authentication system -type feat
   - @sdd-specify payment timeout issue -type fix
   - @sdd-specify api documentation update -type docs

   Please provide a valid branch type or omit -type to use the default (feat).
   ```

   Do not proceed with any further steps if validation fails.

0.1. **Construct Full Feature Description**: Combine the branch type with the feature description to create the full branch-ready description:

- Format: `<type>/<feature_description>`
- Example: If `-type fix` and feature description is "payment timeout issue", construct `fix/payment timeout issue`
- If `-type` was not provided, use `feat/<feature_description>`

  0.2. **Summarize the feature description**: Create a concise summary of the full feature description (including type prefix) that is 80 characters or less. This summary should capture the essential meaning while preserving the required type prefix and being suitable for use as a git branch name. Preserve key technical terms and maintain clarity.

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

1.5. **Load Reference Folder (if provided)**: If the user specified a reference folder with `-ref <folder_name>`:

- Check if `.specify/reference/<folder_name>/` exists
- **If folder does NOT exist**, immediately stop and return:

  ```
  ERROR: Reference folder not found

  Requested folder: .specify/reference/<folder_name>/

  Available reference folders:
  [List all subdirectories in .specify/reference/]

  To create a new reference folder, see the "Creating Reference Folders" section in the specify command documentation.
  ```

  STOP all processing and do not continue.

- **If folder exists**, load all files in the folder. Extract and summarize:
  - **Architecture & Patterns**: Code patterns, design decisions, and conventions found
  - **Code Examples & Interfaces**: API signatures, interfaces, or code patterns to follow
  - **Configuration & Setup**: Configuration patterns, environment setup, dependencies
  - **Testing Approaches**: Testing patterns, fixtures, or utilities available
  - **Key Technical Decisions**: Important technical choices and rationale

Store this as REFERENCE_CONTEXT for inclusion in the spec.

2. Load `.specify/templates/spec-template.md` to understand required sections.

3. Write the specification to SPEC_FILE using the template structure, replacing placeholders with concrete details derived from the feature description and reference folder (if provided) while preserving section order and headings.

   **If a reference folder was used**, include a **Reference Metadata section at the very top** (before the main feature title):

   ```markdown
   > **Reference**: `<folder_name>`
   >
   > _This specification uses context from the reference folder to ensure consistency with existing patterns and implementations._

   # [Feature Title]

   [Rest of spec content...]
   ```

   And include a **Reference Context section after User Stories**:

   ```markdown
   ## Reference Context

   **Reference Folder**: `<folder_name>`
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

   This allows @sdd-plan and @sdd-tasks to use the pre-analyzed insights without re-loading files.

4. Report completion with branch name, spec file path, reference folder used (if any), and readiness for the next phase.

Note: The script creates the feature directory and initializes the spec file.

### Output Format

Upon successful completion, display the following information:

```
✅ Feature specification created successfully

Branch Name: <BRANCH_NAME>
Spec File: <SPEC_FILE>
Reference: <folder_name> (if used, otherwise "None")

Next Steps:
- Review the specification in the spec file
- Run @sdd-plan to create an implementation plan
- Run @sdd-tasks to break down into actionable tasks
```

**Example**:

```
✅ Feature specification created successfully

Branch Name: feat/user-authentication-system
Spec File: .specify/features/feat-user-authentication-system/spec.md
Reference: auth-patterns

Next Steps:
- Review the specification in the spec file
- Run @sdd-plan to create an implementation plan
- Run @sdd-tasks to break down into actionable tasks
```

---

## Creating Reference Folders

Reference folders provide reusable context that enhances @sdd-specify, @sdd-plan, and @sdd-tasks workflows.

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
   - `@sdd-specify <description> -ref <folder_name>` - Creates spec with reference context
   - The reference folder name is stored in the spec's markdown metadata
   - `@sdd-plan` and `@sdd-tasks` automatically source the reference from the spec file
   - No need to pass `-ref` to @sdd-plan or @sdd-tasks - they read it from the spec

### Important Notes

- Only create the folder if it doesn't already exist
- The folder name should be descriptive and kebab-case (e.g., `user-authentication`, `payment-system`)
- The README.md serves as the primary reference document but additional files can be added
- Reference folders provide consistent context: define once in @sdd-specify, automatically used by @sdd-plan and @sdd-tasks
