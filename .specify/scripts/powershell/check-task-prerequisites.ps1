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
        Write-Host "Usage: .\check-task-prerequisites.ps1 [<feature-name>] [--json]"
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
. "$scriptDir/common.ps1"

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
$featureDir = $paths.FEATURE_DIR
$implPlan = $paths.IMPL_PLAN
$research = $paths.RESEARCH
$dataModel = $paths.DATA_MODEL
$contractsDir = $paths.CONTRACTS_DIR
$quickstart = $paths.QUICKSTART

if (-not (Test-Path $featureDir -PathType Container)) {
    Write-Error "ERROR: Feature directory not found: $featureDir"
    Write-Error "Run /specify first."
    exit 1
}

if (-not (Test-Path $implPlan)) {
    Write-Error "ERROR: plan.md not found in $featureDir"
    Write-Error "Run /plan first."
    exit 1
}

if ($jsonMode) {
    $docs = @()
    if (Test-Path $research) { $docs += "research.md" }
    if (Test-Path $dataModel) { $docs += "data-model.md" }
    if ((Test-Path $contractsDir -PathType Container) -and (Get-ChildItem $contractsDir -ErrorAction SilentlyContinue)) {
        $docs += "contracts/"
    }
    if (Test-Path $quickstart) { $docs += "quickstart.md" }
    
    $output = @{
        FEATURE_DIR = $featureDir
        AVAILABLE_DOCS = $docs
    } | ConvertTo-Json -Compress
    
    Write-Output $output
} else {
    Write-Output "FEATURE_DIR:$featureDir"
    Write-Output "AVAILABLE_DOCS:"
    Write-Output (Test-FileExists -Path $research -Label "research.md")
    Write-Output (Test-FileExists -Path $dataModel -Label "data-model.md")
    Write-Output (Test-DirExists -Path $contractsDir -Label "contracts/")
    Write-Output (Test-FileExists -Path $quickstart -Label "quickstart.md")
}
