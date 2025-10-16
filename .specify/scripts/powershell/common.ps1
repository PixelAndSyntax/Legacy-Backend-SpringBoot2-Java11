# Common functions and variables for all PowerShell scripts
# Cross-platform compatible PowerShell functions
#Requires -Version 5.1

# Set consistent encoding for cross-platform compatibility
# PowerShell 5.1 compatible encoding setup with error handling
try {
    if ($PSVersionTable.PSVersion.Major -ge 6) {
        # PowerShell Core (6+) handles this automatically
        [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
        $OutputEncoding = [Console]::OutputEncoding
    } else {
        # PowerShell 5.1 on Windows
        if ($PSVersionTable.Platform -eq $null -or $PSVersionTable.Platform -eq 'Win32NT') {
            [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
            $OutputEncoding = [Console]::OutputEncoding
        } else {
            # Fallback for older versions or non-Windows
            $OutputEncoding = [System.Text.Encoding]::UTF8
        }
    }
} catch {
    # Fallback if console encoding fails
    $OutputEncoding = [System.Text.Encoding]::UTF8
}

# Cross-platform path helper function
function Join-CrossPlatformPath {
    param([string[]]$PathParts)
    
    if ($PathParts.Count -eq 0) {
        return ""
    }
    
    $result = $PathParts[0]
    for ($i = 1; $i -lt $PathParts.Count; $i++) {
        $result = Join-Path $result $PathParts[$i]
    }
    
    return $result
}

# Cross-platform directory separator
$script:DirectorySeparator = [System.IO.Path]::DirectorySeparatorChar

function Get-RepoRoot {
    try {
        $result = git rev-parse --show-toplevel 2>$null
        if ($LASTEXITCODE -eq 0) {
            return $result
        }
    } catch {
        # Fallback to current directory
    }
    return (Get-Location).Path
}

function Get-CurrentBranch {
    try {
        $result = git rev-parse --abbrev-ref HEAD 2>$null
        if ($LASTEXITCODE -eq 0) {
            return $result
        }
    } catch {
        # Fallback to 'main' if not in git repo
    }
    return "main"
}

function Test-FeatureBranch {
    param([string]$Branch)
    
    if ($Branch -eq "main" -or $Branch -eq "master") {
        Write-Error "ERROR: Not on a feature branch. Current branch: $Branch"
        Write-Error "Feature branches should be named like: feature-name"
        return $false
    }
    return $true
}

function Get-FeatureDir {
    param(
        [string]$RepoRoot,
        [string]$FeatureName
    )
    return (Join-CrossPlatformPath @($RepoRoot, "specs", $FeatureName))
}

# Determine which feature to use (by name or auto-detect)
# Args: $FeatureName = optional feature name
# Returns: feature name to use
# Exits with error if multiple features exist and none specified
function Get-DeterminedFeature {
    param([string]$FeatureName = "")
    
    $repoRoot = Get-RepoRoot
    $specsDir = (Join-CrossPlatformPath @($repoRoot, "specs"))
    
    # If feature name provided, validate it exists
    if ($FeatureName) {
        if (-not (Test-Path (Join-CrossPlatformPath @($specsDir, $FeatureName)) -PathType Container)) {
            Write-Error "ERROR: Feature '$FeatureName' not found in $specsDir"
            Write-Error "Available features:"
            Get-ChildItem -Path $specsDir -Directory -ErrorAction SilentlyContinue | ForEach-Object {
                Write-Error "  - $($_.Name)"
            }
            throw "Feature not found"
        }
        return $FeatureName
    }
    
    # No feature name provided - check what's available
    if (-not (Test-Path $specsDir -PathType Container)) {
        Write-Error "ERROR: Specs directory not found: $specsDir"
        throw "Specs directory not found"
    }
    
    $features = Get-ChildItem -Path $specsDir -Directory -ErrorAction SilentlyContinue
    $featureCount = ($features | Measure-Object).Count
    
    if ($featureCount -eq 0) {
        Write-Error "ERROR: No features found in $specsDir"
        Write-Error "Run @specify first to create a feature specification."
        throw "No features found"
    }
    elseif ($featureCount -eq 1) {
        # Auto-select single feature
        return $features[0].Name
    }
    else {
        # Multiple features - need user to specify
        Write-Error "ERROR: Multiple specs found. Please specify which feature to use:"
        Write-Error ""
        Write-Error "Available features:"
        $features | ForEach-Object {
            Write-Error "  - $($_.Name)"
        }
        Write-Error ""
        Write-Error "Usage: @plan <feature-name>, @tasks <feature-name>, or @implement <feature-name>"
        throw "Multiple features found"
    }
}

function Get-FeaturePaths {
    param([string]$FeatureName = "")
    
    $repoRoot = Get-RepoRoot
    $currentBranch = Get-CurrentBranch
    
    # Use provided name or fallback to branch
    if (-not $FeatureName) {
        $FeatureName = $currentBranch
    }
    
    $featureDir = Get-FeatureDir -RepoRoot $repoRoot -FeatureName $FeatureName
    
    return @{
        REPO_ROOT = $repoRoot
        CURRENT_BRANCH = $currentBranch
        FEATURE_NAME = $FeatureName
        FEATURE_DIR = $featureDir
        FEATURE_SPEC = (Join-CrossPlatformPath @($featureDir, "spec.md"))
        IMPL_PLAN = (Join-CrossPlatformPath @($featureDir, "plan.md"))
        TASKS = (Join-CrossPlatformPath @($featureDir, "tasks.md"))
        RESEARCH = (Join-CrossPlatformPath @($featureDir, "research.md"))
        DATA_MODEL = (Join-CrossPlatformPath @($featureDir, "data-model.md"))
        QUICKSTART = (Join-CrossPlatformPath @($featureDir, "quickstart.md"))
        CONTRACTS_DIR = (Join-CrossPlatformPath @($featureDir, "contracts"))
    }
}

function Test-FileExists {
    param(
        [string]$Path,
        [string]$Label
    )
    
    if (Test-Path $Path -PathType Leaf) {
        return "  [OK] $Label"
    } else {
        return "  [X] $Label"
    }
}

function Test-DirExists {
    param(
        [string]$Path,
        [string]$Label
    )
    
    if ((Test-Path $Path -PathType Container) -and (Get-ChildItem $Path -ErrorAction SilentlyContinue)) {
        return "  [OK] $Label"
    } else {
        return "  [X] $Label"
    }
}
