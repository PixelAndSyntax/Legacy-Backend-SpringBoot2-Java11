#Requires -Version 5.1

param(
    [Parameter(Position=0)]
    [string]$TargetDir
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($TargetDir)) {
    Write-Error "Usage: npm run spec:tasks <spec-folder>"
    exit 1
}

if (-not (Test-Path $TargetDir -PathType Container)) {
    Write-Error "Spec folder not found: $TargetDir"
    exit 1
}

if (Test-Path (Join-Path $TargetDir "tasks.md")) {
    Write-Error "tasks.md already exists in $TargetDir"
    exit 1
}

Copy-Item -Path (Join-Path ".specify" (Join-Path "templates" "tasks-template.md")) -Destination (Join-Path $TargetDir "tasks.md")
Write-Output "Added tasks.md to $TargetDir"
