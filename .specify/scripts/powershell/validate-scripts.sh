#!/bin/bash
# Validate PowerShell scripts for common syntax issues

echo "🔍 Validating PowerShell Scripts..."
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ERRORS=0
WARNINGS=0

# Function to check for common issues
check_script() {
    local file="$1"
    local basename=$(basename "$file")
    local issues=()
    
    echo "Checking: $basename"
    
    # Check 1: Proper parameter blocks
    if grep -q 'param(' "$file" && ! grep -q '^param(' "$file"; then
        issues+=("  ⚠️  param() block should be at the start of the script")
        WARNINGS=$((WARNINGS + 1))
    fi
    
    # Check 2: Closing braces match opening braces
    local open=$(grep -o '{' "$file" | wc -l)
    local close=$(grep -o '}' "$file" | wc -l)
    if [ "$open" -ne "$close" ]; then
        issues+=("  ❌ Brace mismatch: $open opening vs $close closing")
        ERRORS=$((ERRORS + 1))
    fi
    
    # Check 3: Check for unmatched quotes (basic check)
    local double_quotes=$(grep -o '"' "$file" | wc -l)
    if [ $((double_quotes % 2)) -ne 0 ]; then
        issues+=("  ⚠️  Possible unmatched double quotes")
        WARNINGS=$((WARNINGS + 1))
    fi
    
    # Check 4: ErrorActionPreference should be set
    if ! grep -q 'ErrorActionPreference' "$file"; then
        issues+=("  ℹ️  Consider setting ErrorActionPreference")
    fi
    
    # Print issues
    if [ ${#issues[@]} -eq 0 ]; then
        echo "  ✅ No issues found"
    else
        for issue in "${issues[@]}"; do
            echo "$issue"
        done
    fi
    echo ""
}

# Check all PowerShell scripts
for script in "$SCRIPT_DIR"/*.ps1; do
    if [ -f "$script" ]; then
        check_script "$script"
    fi
done

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Validation Summary:"
echo "  ❌ Errors: $ERRORS"
echo "  ⚠️  Warnings: $WARNINGS"

if [ $ERRORS -eq 0 ]; then
    echo ""
    echo "✅ All scripts passed basic validation!"
    echo ""
    echo "Note: This is a basic syntax check. For full validation:"
    echo "  - Run on Windows with PowerShell 5.1+ or PowerShell Core 7+"
    echo "  - Use: Get-Command -Name .\script.ps1 -Syntax"
    exit 0
else
    echo ""
    echo "❌ Some scripts have errors that need to be fixed."
    exit 1
fi
