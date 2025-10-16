#Requires -Version 5.1

param(
    [Parameter(Position=0)]
    [string]$AgentType = ""
)

$ErrorActionPreference = "Stop"

$repoRoot = git rev-parse --show-toplevel
$currentBranch = git rev-parse --abbrev-ref HEAD
$featureDir = (Join-Path $repoRoot (Join-Path "specs" $currentBranch))
$newPlan = (Join-Path $featureDir "plan.md")

$claudeFile = (Join-Path $repoRoot "CLAUDE.md")
$geminiFile = (Join-Path $repoRoot "GEMINI.md")
$copilotFile = (Join-Path $repoRoot (Join-Path ".github" "copilot-instructions.md"))
$cursorFile = (Join-Path $repoRoot (Join-Path ".cursor" (Join-Path "rules" "specify-rules.mdc")))
$qwenFile = (Join-Path $repoRoot "QWEN.md")
$agentsFile = (Join-Path $repoRoot "AGENTS.md")

if (-not (Test-Path $newPlan)) {
    Write-Error "ERROR: No plan.md found at $newPlan"
    exit 1
}

Write-Output "=== Updating agent context files for feature $currentBranch ==="

# Extract information from plan.md
$newLang = (Select-String -Path $newPlan -Pattern '^\*\*Language/Version\*\*: ' | Select-Object -First 1).Line -replace '^\*\*Language/Version\*\*: ', '' | Where-Object { $_ -notmatch 'NEEDS CLARIFICATION' }
$newFramework = (Select-String -Path $newPlan -Pattern '^\*\*Primary Dependencies\*\*: ' | Select-Object -First 1).Line -replace '^\*\*Primary Dependencies\*\*: ', '' | Where-Object { $_ -notmatch 'NEEDS CLARIFICATION' }
$newDb = (Select-String -Path $newPlan -Pattern '^\*\*Storage\*\*: ' | Select-Object -First 1).Line -replace '^\*\*Storage\*\*: ', '' | Where-Object { $_ -notmatch 'N/A|NEEDS CLARIFICATION' }
$newProjectType = (Select-String -Path $newPlan -Pattern '^\*\*Project Type\*\*: ' | Select-Object -First 1).Line -replace '^\*\*Project Type\*\*: ', ''

function Update-AgentFile {
    param(
        [string]$TargetFile,
        [string]$AgentName
    )
    
    Write-Output "Updating $AgentName context file: $TargetFile"
    
    if (-not (Test-Path $TargetFile)) {
        Write-Output "Creating new $AgentName context file..."
        
        $templateFile = "$repoRoot/.specify/templates/agent-file-template.md"
        if (-not (Test-Path $templateFile)) {
            Write-Error "ERROR: Template not found"
            return $false
        }
        
        $content = Get-Content $templateFile -Raw -Encoding UTF8
        
        # Replace placeholders
        $projectName = Split-Path $repoRoot -Leaf
        $date = Get-Date -Format "yyyy-MM-dd"
        $content = $content -replace '\[PROJECT NAME\]', $projectName
        $content = $content -replace '\[DATE\]', $date
        $content = $content -replace '\[EXTRACTED FROM ALL PLAN.MD FILES\]', "- $newLang + $newFramework ($currentBranch)"
        
        # Determine project structure
        if ($newProjectType -match 'web') {
            $structure = "backend/`nfrontend/`ntests/"
        } else {
            $structure = "src/`ntests/"
        }
        $content = $content -replace '\[ACTUAL STRUCTURE FROM PLANS\]', $structure
        
        # Determine commands based on language
        $commands = "# Add commands for $newLang"
        if ($newLang -match 'Python') {
            $commands = "cd src && pytest && ruff check ."
        } elseif ($newLang -match 'Rust') {
            $commands = "cargo test && cargo clippy"
        } elseif ($newLang -match 'JavaScript|TypeScript') {
            $commands = "npm test && npm run lint"
        }
        $content = $content -replace '\[ONLY COMMANDS FOR ACTIVE TECHNOLOGIES\]', $commands
        
        $content = $content -replace '\[LANGUAGE-SPECIFIC, ONLY FOR LANGUAGES IN USE\]', "$newLang`: Follow standard conventions"
        $content = $content -replace '\[LAST 3 FEATURES AND WHAT THEY ADDED\]', "- $currentBranch`: Added $newLang + $newFramework"
        
        $content | Out-File -FilePath $TargetFile -Encoding UTF8
        
    } else {
        Write-Output "Updating existing $AgentName context file..."
        
        # Save manual additions if they exist
        $content = Get-Content $TargetFile -Raw -Encoding UTF8
        $manualStart = $content.IndexOf('<!-- MANUAL ADDITIONS START -->')
        $manualEnd = $content.IndexOf('<!-- MANUAL ADDITIONS END -->')
        $manualContent = ""
        
        if ($manualStart -ge 0 -and $manualEnd -ge 0) {
            $manualContent = $content.Substring($manualStart, $manualEnd - $manualStart + ('<!-- MANUAL ADDITIONS END -->').Length)
        }
        
        # Update Active Technologies section
        if ($content -match '## Active Technologies\r?\n(.*?)\r?\n\r?\n') {
            $existingTech = $Matches[1]
            $additions = @()
            
            if ($newLang -and $existingTech -notmatch [regex]::Escape($newLang)) {
                $additions += "- $newLang + $newFramework ($currentBranch)"
            }
            if ($newDb -and $newDb -ne 'N/A' -and $existingTech -notmatch [regex]::Escape($newDb)) {
                $additions += "- $newDb ($currentBranch)"
            }
            
            if ($additions.Count -gt 0) {
                $newBlock = $existingTech + "`n" + ($additions -join "`n")
                $content = $content -replace '## Active Technologies\r?\n.*?\r?\n\r?\n', "## Active Technologies`n$newBlock`n`n"
            }
        }
        
        # Update Recent Changes section
        if ($content -match '## Recent Changes\r?\n(.*?)(\r?\n\r?\n|$)') {
            $existingChanges = $Matches[1].Trim() -split '\r?\n' | Where-Object { $_ }
            $lines = @("- $currentBranch`: Added $newLang + $newFramework") + $existingChanges
            $lines = $lines | Select-Object -First 3
            $content = $content -replace '## Recent Changes\r?\n.*?(\r?\n\r?\n|$)', "## Recent Changes`n$($lines -join "`n")`n`n"
        }
        
        # Update date
        $date = Get-Date -Format "yyyy-MM-dd"
        $content = $content -replace 'Last updated: \d{4}-\d{2}-\d{2}', "Last updated: $date"
        
        # Remove old manual additions and append saved ones
        if ($manualContent) {
            $content = $content -replace '<!-- MANUAL ADDITIONS START -->.*?<!-- MANUAL ADDITIONS END -->', '', 'Singleline'
            $content = $content.TrimEnd() + "`n`n" + $manualContent
        }
        
        $content | Out-File -FilePath $TargetFile -Encoding UTF8
    }
    
    Write-Output "✅ $AgentName context file updated successfully"
    return $true
}

switch ($AgentType) {
    "claude" { Update-AgentFile -TargetFile $claudeFile -AgentName "Claude Code" }
    "gemini" { Update-AgentFile -TargetFile $geminiFile -AgentName "Gemini CLI" }
    "copilot" { Update-AgentFile -TargetFile $copilotFile -AgentName "GitHub Copilot" }
    "cursor" { Update-AgentFile -TargetFile $cursorFile -AgentName "Cursor IDE" }
    "qwen" { Update-AgentFile -TargetFile $qwenFile -AgentName "Qwen Code" }
    "opencode" { Update-AgentFile -TargetFile $agentsFile -AgentName "opencode" }
    "" {
        # Update all existing files or create Claude file if none exist
        $anyUpdated = $false
        if (Test-Path $claudeFile) { Update-AgentFile -TargetFile $claudeFile -AgentName "Claude Code"; $anyUpdated = $true }
        if (Test-Path $geminiFile) { Update-AgentFile -TargetFile $geminiFile -AgentName "Gemini CLI"; $anyUpdated = $true }
        if (Test-Path $copilotFile) { Update-AgentFile -TargetFile $copilotFile -AgentName "GitHub Copilot"; $anyUpdated = $true }
        if (Test-Path $cursorFile) { Update-AgentFile -TargetFile $cursorFile -AgentName "Cursor IDE"; $anyUpdated = $true }
        if (Test-Path $qwenFile) { Update-AgentFile -TargetFile $qwenFile -AgentName "Qwen Code"; $anyUpdated = $true }
        if (Test-Path $agentsFile) { Update-AgentFile -TargetFile $agentsFile -AgentName "opencode"; $anyUpdated = $true }
        
        if (-not $anyUpdated) {
            Update-AgentFile -TargetFile $claudeFile -AgentName "Claude Code"
        }
    }
    default {
        Write-Error "ERROR: Unknown agent type '$AgentType' (expected claude|gemini|copilot|cursor|qwen|opencode)"
        exit 1
    }
}

Write-Output ""
Write-Output "Summary of changes:"
if ($newLang) { Write-Output "- Added language: $newLang" }
if ($newFramework) { Write-Output "- Added framework: $newFramework" }
if ($newDb -and $newDb -ne "N/A") { Write-Output "- Added database: $newDb" }
Write-Output ""
Write-Output "Usage: .\update-agent-context.ps1 [claude|gemini|copilot|cursor|qwen|opencode]"
