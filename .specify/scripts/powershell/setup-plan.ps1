#Requires -Version 5.1

param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$Arguments
)

$ErrorActionPreference = "Stop"

$jsonMode = $false
$featureName = ""

foreach ($arg in $Arguments) {
    if ($arg -eq "--json") {
        $jsonMode = $true
    }
    elseif ($arg -eq "--help" -or $arg -eq "-h") {
        Write-Host "Usage: .\setup-plan.ps1 [<feature-name>] [--json]"
        Write-Host "  <feature-name>  Optional feature name (auto-detects if omitted)"
        Write-Host "  --json          Output in JSON format"
        exit 0
    }
    else {
        if ([string]::IsNullOrWhiteSpace($featureName)) {
            $featureName = $arg
        }
    }
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $scriptDir "common.ps1")

# Determine which feature to use
if ([string]::IsNullOrWhiteSpace($featureName)) {
    try {
        $featureName = Get-DeterminedFeature
    } catch {
        exit 1
    }
} else {
    try {
        $null = Get-DeterminedFeature -FeatureName $featureName
    } catch {
        exit 1
    }
}

$paths = Get-FeaturePaths -FeatureName $featureName
$repoRoot = $paths.REPO_ROOT
$featureDir = $paths.FEATURE_DIR
$featureSpec = $paths.FEATURE_SPEC
$implPlan = $paths.IMPL_PLAN
$currentBranch = $paths.CURRENT_BRANCH

if (-not (Test-Path $featureDir)) {
    New-Item -ItemType Directory -Path $featureDir -Force | Out-Null
}

$template = (Join-Path $repoRoot (Join-Path ".specify" (Join-Path "templates" "plan-template.md")))
if (Test-Path $template) {
    Copy-Item -Path $template -Destination $implPlan
}

if ($jsonMode) {
    $output = @{
        FEATURE_SPEC = $featureSpec
        IMPL_PLAN = $implPlan
        SPECS_DIR = $featureDir
        BRANCH = $currentBranch
    } | ConvertTo-Json -Compress
    
    Write-Output $output
} else {
    Write-Output "FEATURE_SPEC: $featureSpec"
    Write-Output "IMPL_PLAN: $implPlan"
    Write-Output "SPECS_DIR: $featureDir"
    Write-Output "BRANCH: $currentBranch"
}
