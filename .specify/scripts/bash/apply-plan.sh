#!/bin/bash
# Copyright (c) [https://github.com/github/spec-kit]
# Modified by Trentin Barnard, 2025
# MIT License

set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: npm run spec:plan <spec-folder>" >&2
  exit 1
fi
TARGET_DIR="$1"
if [[ ! -d "$TARGET_DIR" ]]; then
  echo "Spec folder not found: $TARGET_DIR" >&2
  exit 1
fi
if [[ -f "$TARGET_DIR/plan.md" ]]; then
  echo "plan.md already exists in $TARGET_DIR" >&2
  exit 1
fi
cp .specify/templates/plan-template.md "$TARGET_DIR/plan.md"
echo "Added plan.md to $TARGET_DIR"
