---
name: task_completion
description: Exact validation steps before marking a coding task complete
metadata:
  type: reference
---

# Task Completion Checklist

Before marking any coding task as complete, run these validation steps in order:

## 1. Kotlin Compilation

```bash
./gradlew compileDebugKotlin
```

**Must pass**: No compilation errors. Catches type errors, missing imports, syntax issues.

## 2. Lint Checks

```bash
./gradlew lintDebug
```

**Must pass**: No new lint errors. Review warnings for actionable issues (ignore pre-existing project-wide warnings).

Lint report: `app/build/reports/lint-results-debug.html`

## 3. Build Verification

```bash
./gradlew assembleDebug --warning-mode=all
```

**Must pass**: Full debug build succeeds. Surfaces deprecation warnings and build config issues.

## 4. Code Review Against Specs

Per Trellis workflow (Phase 2.2), verify changes against:
- Relevant `.trellis/spec/` guidelines
- Task `prd.md` acceptance criteria
- Task `design.md` technical contracts (if exists)
- Task `implement.md` checklist items (if exists)

For multi-package changes, load each package's spec index Quality Check section via:
```bash
python3 ./.trellis/scripts/get_context.py --mode packages
```

## 5. Manual Testing (UI changes only)

For UI/frontend changes:
- Install to device: `./gradlew installDebug`
- Test happy path + edge cases for the feature
- Monitor for regressions in existing features

**Critical**: Type checking and tests verify code correctness, not feature correctness.

## 6. Trellis Spec Update

Before commit, check if task produced knowledge worth recording:
```bash
# Load the spec update skill
/trellis:update-spec  # or invoke via skill system
```

Update `.trellis/spec/` for:
- New patterns/conventions discovered
- Pitfalls hit during implementation
- Technical decisions made

## 7. Git Status Check

```bash
git status --porcelain
```

Verify:
- All intended changes staged
- No unintended files dirty
- No secrets in staged files (.env, credentials, etc.)

## When to Skip Steps

- Skip step 5 (manual testing) for backend-only changes
- Skip step 6 (spec update) for trivial changes that introduce no new knowledge
- Never skip steps 1-3 (compilation, lint, build)

## Why

Trellis workflow requires verification before reporting completion (Phase 2.2). Final Phase 2.2 pass must be full-scope, not just latest changes. Spec updates belong in same task commit, not forgotten follow-ups (Phase 3.4 preamble).
