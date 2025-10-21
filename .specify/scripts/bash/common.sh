#!/bin/bash
# Copyright (c) [https://github.com/github/spec-kit]
# Modified by Trentin Barnard, 2025
# MIT License

# (Moved to scripts/bash/) Common functions and variables for all scripts

get_repo_root() { git rev-parse --show-toplevel; }
get_current_branch() { git rev-parse --abbrev-ref HEAD; }

check_feature_branch() {
    local branch="$1"
    if [[ "$branch" == "main" || "$branch" == "master" ]]; then
        echo "ERROR: Not on a feature branch. Current branch: $branch" >&2
        echo "Feature branches should be named like: feature-name" >&2
        return 1
    fi; return 0
}

get_feature_dir() { echo "$1/specs/$2"; }

# Determine which feature to use (by name or auto-detect)
# Args: $1 = optional feature name
# Returns: feature name to use (prints to stdout)
# Exits with error if multiple features exist and none specified
determine_feature() {
    local repo_root=$(get_repo_root)
    local specs_dir="$repo_root/specs"
    local feature_name="$1"
    
    # If feature name provided, validate it exists
    if [[ -n "$feature_name" ]]; then
        if [[ ! -d "$specs_dir/$feature_name" ]]; then
            echo "ERROR: Feature '$feature_name' not found in $specs_dir" >&2
            echo "Available features:" >&2
            ls -1 "$specs_dir" 2>/dev/null | sed 's/^/  - /' >&2
            return 1
        fi
        echo "$feature_name"
        return 0
    fi
    
    # No feature name provided - check what's available
    if [[ ! -d "$specs_dir" ]]; then
        echo "ERROR: Specs directory not found: $specs_dir" >&2
        return 1
    fi
    
    local feature_count=$(ls -1 "$specs_dir" 2>/dev/null | wc -l | tr -d ' ')
    
    if [[ "$feature_count" -eq 0 ]]; then
        echo "ERROR: No features found in $specs_dir" >&2
        echo "Run @sdd-specify first to create a feature specification." >&2
        return 1
    elif [[ "$feature_count" -eq 1 ]]; then
        # Auto-select single feature
        ls -1 "$specs_dir" 2>/dev/null | head -1
        return 0
    else
        # Multiple features - need user to specify
        echo "ERROR: Multiple specs found. Please specify which feature to use:" >&2
        echo "" >&2
        echo "Available features:" >&2
        ls -1 "$specs_dir" 2>/dev/null | sed 's/^/  - /' >&2
        echo "" >&2
        echo "Usage: @sdd-plan <feature-name>, @sdd-tasks <feature-name>, or @sdd-implement <feature-name>" >&2
        return 1
    fi
}

get_feature_paths() {
    local repo_root=$(get_repo_root)
    local current_branch=$(get_current_branch)
    local feature_name="${1:-$current_branch}"  # Use provided name or fallback to branch
    local feature_dir=$(get_feature_dir "$repo_root" "$feature_name")
    cat <<EOF
REPO_ROOT='$repo_root'
CURRENT_BRANCH='$current_branch'
FEATURE_NAME='$feature_name'
FEATURE_DIR='$feature_dir'
FEATURE_SPEC='$feature_dir/spec.md'
IMPL_PLAN='$feature_dir/plan.md'
TASKS='$feature_dir/tasks.md'
RESEARCH='$feature_dir/research.md'
DATA_MODEL='$feature_dir/data-model.md'
QUICKSTART='$feature_dir/quickstart.md'
CONTRACTS_DIR='$feature_dir/contracts'
EOF
}

check_file() { [[ -f "$1" ]] && echo "  ✓ $2" || echo "  ✗ $2"; }
check_dir() { [[ -d "$1" && -n $(ls -A "$1" 2>/dev/null) ]] && echo "  ✓ $2" || echo "  ✗ $2"; }
