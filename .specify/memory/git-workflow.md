# Git Workflow Standards

Branch naming, commit messages, and lifecycle management for the Legacy-Backend-SpringBoot2-Java11 project.

---

## Branch Naming Convention

**Pattern**: `type/short-description` | **Length**: 10-50 chars | **Case**: lowercase

**Must**: Follow pattern, valid type, chars (a-z, 0-9, -, /), 10-50 length, from up-to-date main branch  
**Must Not**: Start with numbers, uppercase, spaces/special chars (\_, @, #, $, %, ?, !), invalid types  
**May**: Numbers inside name when relevant (e.g., version/API, migration/java-17)

**Types**: 
- **feat** (new feature/enhancement)
- **fix** (bug fixes)
- **chore** (maintenance, build config)
- **refactor** (code refactoring)
- **test** (test additions/updates)
- **docs** (documentation)
- **migration** (migration-specific changes)
- **hotfix** (critical production fixes)

**Valid**: 
- `feat/add-migration-complexity`
- `fix/acl-method-signature`
- `docs/update-readme`
- `migration/java-17-preparation`
- `test/add-integration-tests`
- `chore/update-dependencies`

**Invalid**: 
- `user-authentication-system` (no type)
- `123-fix-bug` (starts with number)
- `Fix_DB_Bug` (uppercase/underscore)
- `feat/123-add-endpoint` (number immediately after type)
- `new-feature` (too short, no type)

---

## Branch Lifecycle Management

| Branch         | Description                  | Created From | Merge To | Requirements                         |
| -------------- | ---------------------------- | ------------ | -------- | ------------------------------------ |
| main           | Production-ready baseline    | -            | -        | Protected, PR only                   |
| migration/\*   | Migration preparation work   | main         | main     | All tests pass, build successful     |
| feat/\*        | New migration complexities   | main         | main     | Tests pass, documented in README     |
| fix/\*         | Bug fixes                    | main         | main     | Tests pass, issue resolved           |
| docs/\*        | Documentation updates        | main         | main     | Content reviewed, accurate           |
| test/\*        | Test additions/improvements  | main         | main     | All tests pass                       |

**Note**: This project uses **main** as the primary branch (no develop branch). All work branches merge back to main.

---

## Commit Message Standards

**Format**: `[TYPE]: [BRIEF_DESCRIPTION]` | **Max**: 72 chars

**Must**: Follow format, imperative mood (add/fix/update NOT added/fixed/updated), clear/descriptive, match work type, lowercase type  
**Must Not**: Exceed 72 chars for first line, past/continuous tense, sensitive info, vague descriptions

**Types**: feat | fix | chore | refactor | test | docs | migration | hotfix

**Valid Examples**:
- `feat: add CustomAclManager with java.security.acl`
- `fix: resolve ACL method signature compatibility`
- `docs: update README with migration challenges`
- `test: add AsyncService unit tests`
- `chore: configure Maven .mvn directory`
- `migration: add invalid @Async return type patterns`
- `refactor: simplify JAXB custom adapter logic`

**Invalid Examples**:
- `added new feature` (past tense, no format)
- `bug fix` (missing format structure)
- `Updated the readme file with comprehensive migration documentation` (too long)
- `fix stuff` (not descriptive)
- `FEAT: Add Feature` (uppercase type)

---

## Multi-line Commit Messages

For complex changes, use extended commit message format:

```
[TYPE]: [BRIEF_DESCRIPTION]

[DETAILED_EXPLANATION]
- Bullet point 1
- Bullet point 2

[RATIONALE]
```

**Example**:
```
feat: add comprehensive Java 11→17 migration challenges

Enhanced migration complexity with additional deprecated APIs:

Java 11→17 Removed API Challenges:
- Enhanced AsyncService with 6 invalid @Async patterns
- Expanded LegacyThreadService with Thread.destroy(), suspend/resume
- Added legacy library dependencies (commons-beanutils, hibernate-validator)

Spring Boot 2→3 Migration Challenges:
- Added LegacyPropertyConfig with @PropertySource late-binding
- Created legacy-config.properties with deprecated property names
- Expanded application.properties with 25+ deprecated properties

All changes verified: Build successful, 5/5 tests passing
```

---

## Pull Request Standards

| Requirement          | Description                                  | Priority |
| -------------------- | -------------------------------------------- | -------- |
| **Title Format**     | Same as commit message format                | MUST     |
| Description          | Explain what, why, and how                   | MUST     |
| **Linked Issues**    | Reference related issues if applicable       | SHOULD   |
| Test Evidence        | Show test results (e.g., "5/5 tests passing") | MUST     |
| **Breaking Changes** | Clearly mark breaking changes                | MUST     |
| Migration Impact     | Document impact on migration complexity      | SHOULD   |

---

## Git Workflow for Migration Project

### Typical Feature Addition Workflow

1. **Create branch**: `git checkout -b feat/add-new-complexity main`
2. **Make changes**: Add migration complexity (e.g., new deprecated API usage)
3. **Test**: `mvn -s .mvn/settings.xml test` (ensure all tests pass)
4. **Commit**: `git commit -m "feat: add [specific complexity description]"`
5. **Push**: `git push origin feat/add-new-complexity`
6. **Merge**: Merge to main after verification

### Hotfix Workflow

1. **Create branch**: `git checkout -b hotfix/fix-build-error main`
2. **Fix issue**: Resolve critical build or test failure
3. **Test**: `mvn -s .mvn/settings.xml clean install`
4. **Commit**: `git commit -m "fix: resolve [specific issue]"`
5. **Push and merge**: Immediate merge after verification

---

## Versioning and Tagging

| Tag Format     | Description                    | Example         |
| -------------- | ------------------------------ | --------------- |
| v[MAJOR].[MINOR].[PATCH] | Semantic versioning            | v1.0.0          |
| baseline-*     | Baseline snapshots             | baseline-java11 |
| migration-*    | Migration checkpoints          | migration-phase1 |

**Tagging Commands**:
```bash
# Tag baseline
git tag -a baseline-java11 -m "Java 11 baseline with migration complexity"

# Tag migration checkpoint
git tag -a migration-phase1 -m "Phase 1: Dependency updates complete"

# Push tags
git push origin --tags
```

---

## Protected Branch Rules (Recommendations)

| Branch | Rule                          | Enforcement |
| ------ | ----------------------------- | ----------- |
| main   | Require pull request reviews  | SHOULD      |
| main   | Require status checks to pass | MUST        |
| main   | Require linear history        | COULD       |
| main   | Restrict force pushes         | MUST        |

**Note**: For this demonstration project, branch protection is optional but recommended for team collaboration.
