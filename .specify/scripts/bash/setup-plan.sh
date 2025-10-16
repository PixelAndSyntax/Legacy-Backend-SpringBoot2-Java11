#!/usr/bin/env bash
set -e
JSON_MODE=false
FEATURE_NAME=""
for arg in "$@"; do 
    case "$arg" in 
        --json) JSON_MODE=true ;; 
        --help|-h) 
            echo "Usage: $0 [<feature-name>] [--json]"
            echo "  <feature-name>  Optional feature name (auto-detects if omitted)"
            echo "  --json          Output in JSON format"
            exit 0 
            ;; 
        *) 
            if [[ -z "$FEATURE_NAME" ]]; then
                FEATURE_NAME="$arg"
            fi
            ;;
    esac
done
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

# Determine which feature to use
if [[ -z "$FEATURE_NAME" ]]; then
    FEATURE_NAME=$(determine_feature) || exit 1
else
    determine_feature "$FEATURE_NAME" > /dev/null || exit 1
fi

eval $(get_feature_paths "$FEATURE_NAME")
mkdir -p "$FEATURE_DIR"
TEMPLATE="$REPO_ROOT/.specify/templates/plan-template.md"
[[ -f "$TEMPLATE" ]] && cp "$TEMPLATE" "$IMPL_PLAN"
if $JSON_MODE; then
  printf '{"FEATURE_SPEC":"%s","IMPL_PLAN":"%s","SPECS_DIR":"%s","BRANCH":"%s"}\n' \
    "$FEATURE_SPEC" "$IMPL_PLAN" "$FEATURE_DIR" "$CURRENT_BRANCH"
else
  echo "FEATURE_SPEC: $FEATURE_SPEC"; echo "IMPL_PLAN: $IMPL_PLAN"; echo "SPECS_DIR: $FEATURE_DIR"; echo "BRANCH: $CURRENT_BRANCH"
fi
