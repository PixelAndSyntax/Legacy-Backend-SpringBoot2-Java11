#!/bin/bash
# Load Constitution Sections
# Supports preset loading (backend, frontend, infra, core, testing) or explicit section lists

set -euo pipefail

CONSTITUTION_DIR=".specify/templates/constitution"
MEMORY_DIR=".specify/memory"

# Parse input: can be "backend" or "core,testing,security"
INPUT="${1:-core}"

# Function to load a section
load_section() {
  local section="$1"
  local file=""
  
  if [[ "$section" == "branching" ]]; then
    file="$MEMORY_DIR/git-workflow.md"
  else
    file="$CONSTITUTION_DIR/$section.md"
  fi
  
  if [[ -f "$file" ]]; then
    echo ""
    echo "<!-- Constitution Section: $section -->"
    echo ""
    cat "$file"
    echo ""
    echo "---"
    echo ""
  else
    echo "⚠️  Warning: Missing section '$section' (expected at $file)" >&2
  fi
}

# Determine which sections to load
declare -a SECTIONS

if [[ "$INPUT" == *,* ]]; then
  # Explicit section list (comma-separated)
  IFS=',' read -ra SECTIONS <<< "$INPUT"
else
  # Project type preset
  case "$INPUT" in
    backend)
      SECTIONS=("core" "testing" "security" "observability" "architecture" "branching")
      ;;
    frontend)
      SECTIONS=("core" "testing" "architecture" "optional" "branching")
      ;;
    infra)
      SECTIONS=("core" "security" "observability" "branching")
      ;;
    core)
      SECTIONS=("core" "branching")
      ;;
    testing)
      SECTIONS=("testing" "branching")
      ;;
    full)
      SECTIONS=("core" "testing" "security" "observability" "architecture" "optional" "branching")
      ;;
    *)
      echo "Error: Unknown preset '$INPUT'" >&2
      echo "Available presets: backend, frontend, infra, core, testing, full" >&2
      echo "Or use comma-separated section list: core,testing,security" >&2
      exit 1
      ;;
  esac
fi

# Print header
echo "# Constitutional Standards"
echo ""
echo "**Loading Type**: ${INPUT}"
echo "**Sections**: ${SECTIONS[*]}"
echo ""
echo "---"
echo ""

# Load each section
for section in "${SECTIONS[@]}"; do
  load_section "$section"
done

# Print footer with metadata
echo ""
echo "<!-- End of Constitution Loading -->"
echo "<!-- Loaded ${#SECTIONS[@]} sections: ${SECTIONS[*]} -->"
