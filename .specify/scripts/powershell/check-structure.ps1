#Requires -Version 5.1

$ErrorActionPreference = "Stop"

try {
    $rootDir = git rev-parse --show-toplevel 2>$null
} catch {
    $rootDir = "."
}

$pass = $true

$requiredPaths = @(
    "memory/constitution.md",
    ".specify/templates/spec-template.md",
    ".specify/templates/plan-template.md",
    ".specify/templates/tasks-template.md",
    ".specify/templates/commands/specify.md",
    ".specify/templates/commands/plan.md",
    ".specify/templates/commands/tasks.md",
    ".specify/specs/bootstrap/spec.md"
)

Write-Output "[spec:check] Validating required structure..."

foreach ($p in $requiredPaths) {
    $fullPath = Join-Path $rootDir $p
    if (-not (Test-Path $fullPath)) {
        Write-Error "[MISSING] $p"
        $pass = $false
    } else {
        Write-Output "[OK] $p"
    }
}

if ($pass) {
    Write-Output "[spec:check] SUCCESS"
    exit 0
} else {
    Write-Error "[spec:check] FAIL"
    exit 1
}
