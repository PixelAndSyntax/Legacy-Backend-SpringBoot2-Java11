# Git Workflow Standards

Branch naming, commit messages, and lifecycle management for the specify prompt.

---

## Branch Naming Convention

**Pattern**: `type/short-description` | **Length**: 10-50 chars | **Case**: lowercase

**Must**: Follow pattern, valid type, chars (a-z, 0-9, -, /), 10-50 length, from up-to-date develop (except hotfix/maintenance from main)  
**Must Not**: Start with numbers, uppercase, spaces/special chars (\_, @, #, $, %, ?, !), invalid types  
**May**: Numbers inside name when relevant (e.g., version/API)

**Types**: feat (new feature) | fix (bugs) | chore (maintenance) | refactor (code refactoring) | test (tests) | docs (documentation) | hotfix (critical prod) | maintenance (deps/ops)

**Valid**: `feat/add-payment-endpoint`, `fix/handle-auth-timeout`, `docs/update-readme`, `maintenance/update-dependencies`, `feat/add-v2-endpoint`  
**Invalid**: `user-authentication-system` (no type), `123-fix-bug` (starts number), `Fix_DB_Bug` (uppercase/underscore), `feat/123-add-endpoint` (number after type), `new-feature` (too short)

---

## Branch Lifecycle Management

| Branch         | Description           | Created From | Merge To      | Requirements                         |
| -------------- | --------------------- | ------------ | ------------- | ------------------------------------ |
| main           | Production-ready only | -            | -             | Protected, PR only                   |
| develop        | Integration           | -            | main          | Audit, tests pass, review            |
| feature/\*     | New features          | develop      | develop       | Checks pass, PR review, no conflicts |
| hotfix/\*      | Critical fixes        | main         | main, develop | Emergency procedure                  |
| maintenance/\* | Updates, patches      | main/develop | main, develop | Review, tests, security scan         |

---

## Commit Message Standards

**Format**: `[TYPE]: [BRIEF_DESCRIPTION]` | **Max**: 72 chars

**Must**: Follow format, imperative mood (add/fix/update NOT added/fixed/updated), clear/descriptive, match branch type, lowercase type  
**Must Not**: Exceed 72 chars, past/continuous tense, sensitive info, vague

**Types**: feat | fix | chore | refactor | test | docs | hotfix | maintenance (same as branch types)

**Valid**: `feat: add user authentication endpoint`, `fix: resolve timeout issue in payment processing`, `docs: update API documentation for v2.1`, `refactor: simplify error handling logic`, `test: add unit tests for validation module`  
**Invalid**: `added new feature` (past tense), `bug fix` (missing format), `Updated the readme file with new instructions` (too long, wrong tense), `fix stuff` (not descriptive), `FEAT: Add Feature` (uppercase)
