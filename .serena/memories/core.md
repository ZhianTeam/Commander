---
name: core
description: Top-level source map and project overview for Commander Android terminal app
metadata:
  type: project
---

# Commander Project Core

**Commander** is an Android terminal emulator app with an IM-like interface. Currently in 2.0.0-alpha refactor phase, migrating from iApp to Kotlin with Jetpack Compose.

## Project State

**Status**: Early refactor - basic UI scaffolding exists, terminal functionality not yet implemented.

**Key gaps**: No actual command execution yet. README notes "It even cannot run any command."

## Source Organization

- `app/src/main/java/org/zhian/commander/` - main application code
  - `CommanderActivity.kt` - entry point, theme/locale setup
  - `CommanderApplication.kt` - application class
  - `navigation/` - navigation host and routing
  - `ui/` - UI layer organized by feature
    - `splash/` - splash screen
    - `terminal/` - terminal UI (not functional yet)
    - `settings/` - settings screen
    - `theme/` - Material 3 theme definitions
    - `locale/` - localization strings
- `oldie/` - legacy iApp implementation (reference only)
- `.trellis/` - Trellis workflow system (planning, tasks, specs)

## Package Structure

Base package: `org.zhian.commander`

## Related Memories

- `mem:tech_stack` - languages, frameworks, build tools
- `mem:conventions` - Kotlin/Compose coding style
- `mem:suggested_commands` - build, run, lint commands
- `mem:task_completion` - validation steps before marking tasks done

## Why

Project uses Trellis workflow system for structured development. All work goes through planning → implementation → check → spec-update cycle. See `.trellis/workflow.md` for complete process.
