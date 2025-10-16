# Load Constitution Sections
# Supports preset loading (backend, frontend, infra, core, testing) or explicit section lists
# Cross-platform compatible PowerShell script
#Requires -Version 5.1

param(
    [Parameter(Position=0, Mandatory=$false)]
    [string]$Input = "core",
    
    [Parameter(Mandatory=$false)]
    [switch]$Help
)

$ErrorActionPreference = "Stop"

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

$constitutionDir = (Join-CrossPlatformPath @(".specify", "templates", "constitution"))
$memoryDir = (Join-CrossPlatformPath @(".specify", "memory"))

# Show help if requested
if ($Help) {
    Write-Host "=== Constitution Loader Help ===" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "This script loads constitutional standards for software development." -ForegroundColor White
    Write-Host ""
    Write-Host "Usage:" -ForegroundColor Yellow
    Write-Host "  pwsh .\load-constitution.ps1 [preset|sections] [-Help]" -ForegroundColor White
    Write-Host ""
    Write-Host "Presets:" -ForegroundColor Yellow
    Write-Host "  • backend    - Core + Testing + Security + Observability + Architecture + Branching" -ForegroundColor White
    Write-Host "  • frontend   - Core + Testing + Architecture + Optional + Branching" -ForegroundColor White
    Write-Host "  • infra      - Core + Security + Observability + Branching" -ForegroundColor White
    Write-Host "  • core       - Core + Branching (default)" -ForegroundColor White
    Write-Host "  • testing    - Testing + Branching" -ForegroundColor White
    Write-Host "  • full       - All available sections" -ForegroundColor White
    Write-Host ""
    Write-Host "Custom sections (comma-separated):" -ForegroundColor Yellow
    Write-Host "  Available: core, testing, security, observability, architecture, optional, branching" -ForegroundColor White
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Yellow
    Write-Host "  pwsh .\load-constitution.ps1" -ForegroundColor Green
    Write-Host "  pwsh .\load-constitution.ps1 backend" -ForegroundColor Green
    Write-Host "  pwsh .\load-constitution.ps1 `"core,testing,security`"" -ForegroundColor Green
    Write-Host "  pwsh .\load-constitution.ps1 -Help" -ForegroundColor Green
    exit 0
}

# Debug parameter input
Write-Debug "Raw Input Parameter: '$Input'"
Write-Debug "Input Length: $($Input.Length)"
Write-Debug "Args count: $($args.Count)"
if ($args.Count -gt 0) {
    Write-Debug "First arg: '$($args[0])'"
}

# Fallback parameter handling for cross-platform compatibility
if ([string]::IsNullOrWhiteSpace($Input) -and $args.Count -gt 0) {
    Write-Debug "Using fallback parameter from args"
    $Input = $args[0]
}

# Clean and validate input
Write-Debug "Original Input before processing: '$Input'"
Write-Debug "Input type: $($Input.GetType().FullName)"
Write-Debug "Input length before trim: $($Input.Length)"

# Clean the input more aggressively
$Input = $Input.Trim()
if ([string]::IsNullOrWhiteSpace($Input)) {
    Write-Warning "Empty or whitespace-only input detected. Using default 'core'."
    $Input = "core"
} else {
    # Remove any line breaks, carriage returns, and normalize whitespace
    $Input = $Input -replace "`r`n", "" -replace "`n", "" -replace "`r", ""
    # Trim again after line break removal
    $Input = $Input.Trim()
    
    Write-Debug "Cleaned Input: '$Input'"
    Write-Debug "Cleaned Input Length: $($Input.Length)"
    
    # Final validation
    if ([string]::IsNullOrWhiteSpace($Input)) {
        Write-Warning "Input became empty after cleaning. Using default 'core'."
        $Input = "core"
    }
}

# Function to validate section exists
function Test-Section {
    param([string]$Section)
    
    if ($Section -eq "branching") {
        return Test-Path (Join-CrossPlatformPath @($memoryDir, "git-workflow.md"))
    } else {
        return Test-Path (Join-CrossPlatformPath @($constitutionDir, "$Section.md"))
    }
}

# Function to load a section
function Load-Section {
    param([string]$Section)
    
    $file = ""
    
    if ($Section -eq "branching") {
        $file = (Join-CrossPlatformPath @($memoryDir, "git-workflow.md"))
    } else {
        $file = (Join-CrossPlatformPath @($constitutionDir, "$Section.md"))
    }
    
    if (Test-Path $file) {
        Write-Output ""
        Write-Output "<!-- Constitution Section: $Section -->"
        Write-Output ""
        Get-Content $file -Encoding UTF8 | Write-Output
        Write-Output ""
        Write-Output "---"
        Write-Output ""
    } else {
        Write-Warning "Warning: Missing section '$Section' (expected at $file)"
        Write-Host "Available constitution files:" -ForegroundColor Yellow
        if (Test-Path $constitutionDir) {
            Get-ChildItem (Join-CrossPlatformPath @($constitutionDir, "*.md")) | ForEach-Object { 
                Write-Host "  • $($_.BaseName)" -ForegroundColor Gray
            }
        }
        if (Test-Path (Join-CrossPlatformPath @($memoryDir, "git-workflow.md"))) {
            Write-Host "  • branching (from git-workflow.md)" -ForegroundColor Gray
        }
    }
}

# Determine which sections to load
$sections = @()

if ($Input -match ',') {
    # Explicit section list (comma-separated)
    $sections = ($Input -split ',') | ForEach-Object { 
        $section = $_.Trim()
        if (![string]::IsNullOrWhiteSpace($section)) {
            $section
        }
    }
    
    # Filter out empty sections and validate
    $sections = $sections | Where-Object { ![string]::IsNullOrWhiteSpace($_) }
    
    if ($sections.Count -eq 0) {
        Write-Warning "No valid sections found after parsing comma-separated input. Using default 'core'."
        $Input = "core"
    } else {
        # Validate each section exists
        $validSections = @()
        $invalidSections = @()
        
        foreach ($section in $sections) {
            if (Test-Section -Section $section) {
                $validSections += $section
            } else {
                $invalidSections += $section
            }
        }
        
        if ($invalidSections.Count -gt 0) {
            Write-Warning "Invalid sections detected: $($invalidSections -join ', ')"
            Write-Host "Valid sections are: core, testing, security, observability, architecture, optional, branching" -ForegroundColor Yellow
        }
        
        if ($validSections.Count -gt 0) {
            $sections = $validSections
        } else {
            Write-Warning "No valid sections found. Using default 'core'."
            $Input = "core"
            $sections = @()
        }
    }
}

if ($sections.Count -eq 0) {
    # Project type preset
    switch ($Input) {
        "backend" {
            $sections = @("core", "testing", "security", "observability", "architecture", "branching")
        }
        "frontend" {
            $sections = @("core", "testing", "architecture", "optional", "branching")
        }
        "infra" {
            $sections = @("core", "security", "observability", "branching")
        }
        "core" {
            $sections = @("core", "branching")
        }
        "testing" {
            $sections = @("testing", "branching")
        }
        "full" {
            $sections = @("core", "testing", "security", "observability", "architecture", "optional", "branching")
        }
        default {
            Write-Host "=== Constitution Loader Error ===" -ForegroundColor Red
            Write-Host ""
            Write-Host "Unknown preset: '$Input' (length: $($Input.Length))" -ForegroundColor Yellow
            Write-Host ""
            Write-Host "Available presets:" -ForegroundColor Cyan
            Write-Host "  • backend    - Core + Testing + Security + Observability + Architecture + Branching" -ForegroundColor White
            Write-Host "  • frontend   - Core + Testing + Architecture + Optional + Branching" -ForegroundColor White
            Write-Host "  • infra      - Core + Security + Observability + Branching" -ForegroundColor White
            Write-Host "  • core       - Core + Branching (default)" -ForegroundColor White
            Write-Host "  • testing    - Testing + Branching" -ForegroundColor White
            Write-Host "  • full       - All available sections" -ForegroundColor White
            Write-Host ""
            Write-Host "Or use comma-separated section list:" -ForegroundColor Cyan
            Write-Host "  Available sections: core, testing, security, observability, architecture, optional, branching" -ForegroundColor White
            Write-Host ""
            Write-Host "Example usage:" -ForegroundColor Cyan
            Write-Host "  pwsh .\load-constitution.ps1 `"core`"" -ForegroundColor Green
            Write-Host "  pwsh .\load-constitution.ps1 `"backend`"" -ForegroundColor Green
            Write-Host "  pwsh .\load-constitution.ps1 `"core,architecture,testing,branching`"" -ForegroundColor Green
            Write-Host ""
            Write-Host "Debug info:" -ForegroundColor Magenta
            Write-Host "  Raw input: '$($args[0])'" -ForegroundColor Gray
            Write-Host "  Processed input: '$Input'" -ForegroundColor Gray
            Write-Host "  Input type: $($Input.GetType().Name)" -ForegroundColor Gray
            exit 1
        }
    }
}

# Print header
Write-Output "# Constitutional Standards"
Write-Output ""
Write-Output "**Loading Type**: $Input"
Write-Output "**Sections**: $($sections -join ', ')"
Write-Output ""
Write-Output "---"
Write-Output ""

# Load each section
foreach ($section in $sections) {
    Load-Section -Section $section
}

# Print footer with metadata
Write-Output ""
Write-Output "<!-- End of Constitution Loading -->"
Write-Output "<!-- Loaded $($sections.Count) sections: $($sections -join ', ') -->"
