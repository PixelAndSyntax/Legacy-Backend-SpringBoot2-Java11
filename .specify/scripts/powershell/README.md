# PowerShell Scripts for SDD Toolkit

This directory contains PowerShell versions of all bash scripts in the SDD (Specification-Driven Development) toolkit, enabling full support for Windows environments.

## Requirements

- **PowerShell 5.1+** (Windows PowerShell) or **PowerShell 7+** (PowerShell Core - cross-platform)
- **Git** installed and available in PATH
- Repository initialized with `.specify/` structure

## Installation

### Windows (PowerShell 5.1+)

PowerShell 5.1 comes pre-installed on Windows 10/11. No additional installation needed.

### macOS/Linux (PowerShell Core 7+)

```bash
# macOS
brew install powershell

# Ubuntu/Debian
wget -q https://packages.microsoft.com/config/ubuntu/22.04/packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
sudo apt-get update
sudo apt-get install -y powershell

# Run with:
pwsh
```

## Available Scripts

### Core Scripts

| Script                   | Description                    | Usage                                     |
| ------------------------ | ------------------------------ | ----------------------------------------- |
| `common.ps1`             | Shared function library        | Sourced by other scripts                  |
| `create-new-feature.ps1` | Create new feature with branch | `.\create-new-feature.ps1 "Feature Name"` |
| `scaffold-new-spec.ps1`  | Scaffold new spec folder       | `.\scaffold-new-spec.ps1 "Feature Name"`  |
| `get-feature-paths.ps1`  | Display feature paths          | `.\get-feature-paths.ps1`                 |

### Planning & Tasks

| Script            | Description          | Usage                             |
| ----------------- | -------------------- | --------------------------------- |
| `setup-plan.ps1`  | Initialize plan.md   | `.\setup-plan.ps1 [feature-name]` |
| `apply-plan.ps1`  | Apply plan template  | `.\apply-plan.ps1 <spec-folder>`  |
| `apply-tasks.ps1` | Apply tasks template | `.\apply-tasks.ps1 <spec-folder>` |

### Prerequisites & Validation

| Script                                   | Description                            | Usage                                                     |
| ---------------------------------------- | -------------------------------------- | --------------------------------------------------------- |
| `check-structure.ps1`                    | Validate project structure             | `.\check-structure.ps1`                                   |
| `check-task-prerequisites.ps1`           | Check prerequisites for tasks          | `.\check-task-prerequisites.ps1 [feature-name]`           |
| `check-implementation-prerequisites.ps1` | Check prerequisites for implementation | `.\check-implementation-prerequisites.ps1 [feature-name]` |

### Configuration & Context

| Script                     | Description                   | Usage                                     |
| -------------------------- | ----------------------------- | ----------------------------------------- |
| `load-constitution.ps1`    | Load constitutional standards | `.\load-constitution.ps1 [preset]`        |
| `update-agent-context.ps1` | Update agent context files    | `.\update-agent-context.ps1 [agent-type]` |

## Usage Examples

### 1. Create a New Feature

```powershell
# Create new feature and branch
.\create-new-feature.ps1 "User Authentication"

# Output:
# BRANCH_NAME: user-authentication
# SPEC_FILE: /path/to/repo/specs/user-authentication/spec.md
```

### 2. Setup Plan for Feature

```powershell
# Auto-detect feature (if only one exists)
.\setup-plan.ps1

# Or specify feature name
.\setup-plan.ps1 "user-authentication"

# With JSON output
.\setup-plan.ps1 --json
```

### 3. Check Prerequisites

```powershell
# Check if ready for implementation
.\check-implementation-prerequisites.ps1

# Check if ready for tasks
.\check-task-prerequisites.ps1

# With JSON output
.\check-task-prerequisites.ps1 --json
```

### 4. Update Agent Context Files

```powershell
# Update all agent files
.\update-agent-context.ps1

# Update specific agent
.\update-agent-context.ps1 claude
.\update-agent-context.ps1 copilot
.\update-agent-context.ps1 cursor
```

### 5. Load Constitution Standards

```powershell
# Load core standards
.\load-constitution.ps1 core

# Load backend preset
.\load-constitution.ps1 backend

# Load custom sections
.\load-constitution.ps1 "core,testing,security"
```

## Common Options

Most scripts support:

- `--help` or `-h` - Display help information
- `--json` - Output in JSON format (where applicable)
- `[feature-name]` - Optional feature name (auto-detects if omitted)

## JSON Mode

Many scripts support JSON output for programmatic use:

```powershell
$result = .\check-implementation-prerequisites.ps1 --json | ConvertFrom-Json
$featureDir = $result.FEATURE_DIR
$docs = $result.AVAILABLE_DOCS
```

## Error Handling

All scripts use `$ErrorActionPreference = "Stop"` to halt execution on errors. To handle errors gracefully:

```powershell
try {
    .\script.ps1
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
```

## Execution Policy

If you encounter "script cannot be loaded because running scripts is disabled," set the execution policy:

```powershell
# For current user (recommended)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# For current process only (temporary)
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
```

## Integration with Bash Scripts

These PowerShell scripts maintain feature parity with the bash scripts in `.specify/scripts/bash/`. You can use either version depending on your platform:

- **Windows:** Use PowerShell scripts
- **macOS/Linux:** Use bash scripts or PowerShell Core
- **CI/CD:** Choose based on runner OS

## Validation

To validate all scripts:

```bash
# Using provided validation script (requires bash)
./validate-scripts.sh

# Using PowerShell Script Analyzer (requires module)
pwsh -Command "Install-Module PSScriptAnalyzer -Scope CurrentUser -Force"
pwsh -Command "Invoke-ScriptAnalyzer -Path ./*.ps1"
```

See [VALIDATION_REPORT.md](./VALIDATION_REPORT.md) for detailed validation results.

## Troubleshooting

### "Command not found" or "Term is not recognized"

Make sure you're in the correct directory:

```powershell
cd .specify/scripts/powershell
```

### Git commands fail

Ensure git is installed and in your PATH:

```powershell
git --version
```

### Template not found errors

Verify the `.specify/templates/` directory exists with required templates:

```powershell
.\check-structure.ps1
```

### Scripts sourcing common.ps1 fail

Ensure `common.ps1` is in the same directory and the path separator is correct for your OS.

## Contributing

When modifying PowerShell scripts:

1. Test on both Windows PowerShell 5.1 and PowerShell Core 7+
2. Maintain parameter compatibility with bash versions
3. Use `$ErrorActionPreference = "Stop"` for consistent error handling
4. Include help text with `--help` flag
5. Add JSON output mode where applicable
6. Update this README with any new scripts or features

## License

Part of the SDD Toolkit. See main repository LICENSE for details.
