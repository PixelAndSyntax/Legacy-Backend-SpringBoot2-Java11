#Requires -Version 5.1

param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$Arguments
)

$ErrorActionPreference = "Stop"

if ($Arguments.Count -lt 1) {
    Write-Error "Usage: npm run spec:new `"Feature Name`""
    exit 1
}

$featureNameRaw = $Arguments -join " "
$featureSlug = $featureNameRaw.ToLower() -replace '[^a-z0-9]+', '-' -replace '^-|-$', ''

try {
    $rootDir = git rev-parse --show-toplevel 2>$null
} catch {
    $rootDir = "."
}

$specsDir = (Join-Path $rootDir "specs")

if (-not (Test-Path $specsDir)) {
    New-Item -ItemType Directory -Path $specsDir -Force | Out-Null
}

$targetDir = (Join-Path $specsDir $featureSlug)

if (Test-Path $targetDir) {
    Write-Error "Target directory already exists: $targetDir"
    exit 1
}

New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
Copy-Item -Path (Join-Path ".specify" (Join-Path "templates" "spec-template.md")) -Destination (Join-Path $targetDir "spec.md")

# Inject feature name
$specFile = (Join-Path $targetDir "spec.md")
(Get-Content $specFile -Encoding UTF8) -replace '<REPLACE WITH HUMAN-READABLE FEATURE TITLE>', $featureNameRaw | Set-Content $specFile -Encoding UTF8

@"
# $featureNameRaw

This folder contains the specification artifacts for feature: $featureNameRaw

- spec.md – Functional spec
- plan.md – (to be generated) technical implementation plan
- tasks.md – (to be generated) task breakdown
"@ | Out-File -FilePath (Join-Path $targetDir "README.md") -Encoding UTF8

Write-Output "Created spec folder: $targetDir"
