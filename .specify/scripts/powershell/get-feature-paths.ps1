#Requires -Version 5.1

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $scriptDir "common.ps1")

$paths = Get-FeaturePaths
$currentBranch = $paths.CURRENT_BRANCH

if (-not (Test-FeatureBranch -Branch $currentBranch)) {
    exit 1
}

Write-Output "REPO_ROOT: $($paths.REPO_ROOT)"
Write-Output "BRANCH: $($paths.CURRENT_BRANCH)"
Write-Output "FEATURE_DIR: $($paths.FEATURE_DIR)"
Write-Output "FEATURE_SPEC: $($paths.FEATURE_SPEC)"
Write-Output "IMPL_PLAN: $($paths.IMPL_PLAN)"
Write-Output "TASKS: $($paths.TASKS)"
