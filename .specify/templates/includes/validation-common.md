<!--
Copyright (c) Github Speckit
MIT License
-->

# Shared Validation Steps

This file contains common validation procedures used across multiple prompts.

---

## Branching Standards Validation

**CRITICAL**: All operations MUST comply with branching standards.

### Quick Validation Process

1. **Check File Exists**: Verify `.specify/memory/git-workflow.md` exists
2. **Parse Rules**: Extract `allowed_type_prefixes`, `constraints`, and `length_constraints`
3. **Validate Input**: Check input matches pattern and constraints
4. **Report Errors**: Use standard error messages below

### Standard Error Messages

**Missing File Error:**

```
FATAL ERROR: Branching standards file not found.
Required file: .specify/memory/git-workflow.md
Please initialize the constitution first.
```

**Invalid Prefix Error:**

```
ERROR: Invalid branch type prefix.
Branching Standards: .specify/memory/git-workflow.md
Required: type/short-description
Valid prefixes: [from standards]
Your input: [user input]
```

**Constraint Violation Error:**

```
ERROR: Branch name violates branching standards.
Violation: [specific rule violated]
Rule: [rule from standards]
Your input: [user input]
```

---

## Constitution Loading

### Standard Loading Pattern

```bash
# Load constitution based on context
.specify/scripts/bash/load-constitution.sh [preset|sections]
```

**Common Presets:**

- `backend` - Core, testing, security, observability, architecture
- `frontend` - Core, testing, architecture, optional
- `core` - Minimal core + branching only

---

## Feature Directory Validation

### Standard Checks

1. Check if feature directory exists in `.specify/features/`
2. Verify required files exist (spec.md at minimum)
3. Validate file structure matches expected layout

### Standard Error Message

```
ERROR: Feature directory not found.
Expected: .specify/features/[feature-name]/
Available features: [list from directory scan]
```
