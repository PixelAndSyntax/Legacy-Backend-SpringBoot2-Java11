#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: npm run spec:new \"Feature Name\"" >&2
  exit 1
fi
FEATURE_NAME_RAW="$*"
FEATURE_SLUG=$(echo "$FEATURE_NAME_RAW" | tr '[:upper:]' '[:lower:]' | sed -E 's/[^a-z0-9]+/-/g' | sed -E 's/^-|-$//g')

ROOT_DIR=$(git rev-parse --show-toplevel 2>/dev/null || echo ".")
SPECS_DIR="$ROOT_DIR/specs"
mkdir -p "$SPECS_DIR"

TARGET_DIR="$SPECS_DIR/${FEATURE_SLUG}"

if [[ -d "$TARGET_DIR" ]]; then
  echo "Target directory already exists: $TARGET_DIR" >&2
  exit 1
fi

mkdir -p "$TARGET_DIR"
cp .specify/templates/spec-template.md "$TARGET_DIR/spec.md"
# Inject feature name (macOS + GNU sed compatible)
if sed --version >/dev/null 2>&1; then
  sed -i "s|<REPLACE WITH HUMAN-READABLE FEATURE TITLE>|$FEATURE_NAME_RAW|" "$TARGET_DIR/spec.md"
else
  sed -i '' "s|<REPLACE WITH HUMAN-READABLE FEATURE TITLE>|$FEATURE_NAME_RAW|" "$TARGET_DIR/spec.md"
fi

cat > "$TARGET_DIR/README.md" <<EOF
# $FEATURE_NAME_RAW

This folder contains the specification artifacts for feature: $FEATURE_NAME_RAW

- spec.md – Functional spec
- plan.md – (to be generated) technical implementation plan
- tasks.md – (to be generated) task breakdown
EOF

echo "Created spec folder: $TARGET_DIR"
