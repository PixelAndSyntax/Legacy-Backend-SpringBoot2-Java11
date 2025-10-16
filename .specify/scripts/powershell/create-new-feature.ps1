# Create a new feature with branch, directory structure, and template
#Requires -Version 5.1

param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$Arguments
)

$ErrorActionPreference = "Stop"

$jsonMode = $false
$featureArgs = @()

foreach ($arg in $Arguments) {
    if ($arg -eq "--json") {
        $jsonMode = $true
    }
    elseif ($arg -eq "--help" -or $arg -eq "-h") {
        Write-Host "Usage: .\create-new-feature.ps1 [--json] <feature_description>"
        exit 0
    }
    else {
        $featureArgs += $arg
    }
}

$featureDescription = $featureArgs -join " "

if ([string]::IsNullOrWhiteSpace($featureDescription)) {
    Write-Error "Usage: .\create-new-feature.ps1 [--json] <feature_description>"
    exit 1
}

$repoRoot = git rev-parse --show-toplevel
$specsDir = (Join-Path $repoRoot "specs")

if (-not (Test-Path $specsDir)) {
    New-Item -ItemType Directory -Path $specsDir -Force | Out-Null
}

# Convert feature description to branch name
$branchName = $featureDescription.ToLower() -replace '[^a-z0-9]+', '-' -replace '^-|-$', ''

# Create new branch
git checkout -b "$branchName"
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to create branch: $branchName"
    exit 1
}

$featureDir = (Join-Path $specsDir $branchName)
New-Item -ItemType Directory -Path $featureDir -Force | Out-Null

$template = (Join-Path $repoRoot (Join-Path ".specify" (Join-Path "templates" "spec-template.md")))
$specFile = (Join-Path $featureDir "spec.md")

if (Test-Path $template) {
    Copy-Item -Path $template -Destination $specFile
}
else {
    New-Item -ItemType File -Path $specFile -Force | Out-Null
}

if ($jsonMode) {
    $jsonOutput = @{
        BRANCH_NAME = $branchName
        SPEC_FILE = $specFile
    } | ConvertTo-Json -Compress
    Write-Output $jsonOutput
}
else {
    Write-Output "BRANCH_NAME: $branchName"
    Write-Output "SPEC_FILE: $specFile"
}
