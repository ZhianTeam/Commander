---
name: suggested_commands
description: Build, run, lint, and format commands for Commander project
metadata:
  type: reference
---

# Suggested Commands

## Build

```bash
# Make gradlew executable (first time only)
chmod +x ./gradlew

# Build debug APK
./gradlew assembleDebug --no-daemon --warning-mode=all

# Build release APK
./gradlew assembleRelease --no-daemon --warning-mode=all

# Clean build
./gradlew clean
```

## Install & Run

```bash
# Install debug APK to connected device
./gradlew installDebug

# Uninstall
./gradlew uninstallDebug
```

## Code Quality

```bash
# Lint checks
./gradlew lint

# Generate lint report (outputs to app/build/reports/lint-results.html)
./gradlew lintDebug

# Kotlin compile check (catches type errors without full build)
./gradlew compileDebugKotlin
```

## Dependency Management

```bash
# List project dependencies
./gradlew dependencies

# Check for dependency updates
./gradlew dependencyUpdates  # (requires plugin)
```

## Trellis Workflow

```bash
# List available packages/layers with specs
python3 ./.trellis/scripts/get_context.py --mode packages

# Show current active task
python3 ./.trellis/scripts/task.py current --source

# Get detailed phase guidance (e.g., step 1.1)
python3 ./.trellis/scripts/get_context.py --mode phase --step 1.1
```

## Git

```bash
# Standard git workflow
git status
git add <files>
git commit -m "message"
git push origin <branch>
```

## Why

`--no-daemon` avoids Gradle daemon memory overhead on constrained systems. `--warning-mode=all` surfaces deprecation warnings early. Trellis scripts drive structured task workflow per `.trellis/workflow.md`.
