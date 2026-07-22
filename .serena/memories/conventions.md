---
name: conventions
description: Kotlin, Compose, and project-specific coding style for Commander
metadata:
  type: project
---

# Coding Conventions

## Kotlin Style

- Package names: lowercase, no underscores (`org.zhian.commander.ui.theme`)
- Class names: PascalCase (`CommanderActivity`, `ThemeMode`)
- Function names: camelCase (`onCreate`, `stringsFor`)
- Property names: camelCase (`selectedLocale`, `isDark`)
- Enum constants: PascalCase (`ThemeMode.Dark`, `ThemeMode.Light`, `ThemeMode.System`)

## Compose Patterns

### Composable Functions

- Use `@Composable` annotation
- PascalCase naming (e.g., `CommanderTheme`, `CommanderHost`)
- Accept `modifier: Modifier` parameter (typically last parameter unless trailing lambda)
- Use `remember` for state that survives recomposition
- Use `mutableStateOf` for reactive state

### Theme Structure

- Color schemes: private top-level vals (`DarkColorScheme`, `LightColorScheme`)
- Theme functions take `darkTheme: Boolean` parameter with system default
- Material 3 theming via `MaterialTheme` composable

### Localization

- Use `CompositionLocalProvider` + `LocalStrings` for locale management
- Locale state managed at activity root, propagated down via composition local
- String provider function: `stringsFor(locale: Locale)`

## Android Activity Setup

- Enable edge-to-edge with transparent system bars (`SystemBarStyle.auto`)
- Set content via `setContent { }` block
- Theme wraps surface wraps navigation host

## State Management

- UI state hoisted to activity level where needed (locale, theme mode)
- Pass state + change callbacks down (`selectedLocale`, `onLocaleSelected`)
- Derived state calculated inline (`isDark` from `themeMode` + system theme)

## File Organization

- One top-level class per file (Kotlin standard)
- Group related UI by feature in `ui/` subdirectories
- Enums colocated with related composables (e.g., `ThemeMode` in `Theme.kt`)

## Why

Follows Jetpack Compose best practices: hoisted state, composition locals for cross-cutting concerns, Material 3 theming. Edge-to-edge is modern Android UI standard (draws behind system bars).
