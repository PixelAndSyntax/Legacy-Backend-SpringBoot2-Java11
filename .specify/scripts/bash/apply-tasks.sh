#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: npm run spec:tasks <spec-folder>" >&2
  exit 1
fi
TARGET_DIR="$1"
if [[ ! -d "$TARGET_DIR" ]]; then
  echo "Spec folder not found: $TARGET_DIR" >&2
  exit 1
fi
if [[ -f "$TARGET_DIR/tasks.md" ]]; then
  echo "tasks.md already exists in $TARGET_DIR" >&2
  exit 1
fi
cp .specify/templates/tasks-template.md "$TARGET_DIR/tasks.md"
echo "Added tasks.md to $TARGET_DIR"
