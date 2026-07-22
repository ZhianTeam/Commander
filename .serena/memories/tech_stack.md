---
name: tech_stack
description: Language, framework, build tool, and version constraints for Commander
metadata:
  type: reference
---

# Tech Stack

## Language & Runtime

- **Kotlin 2.2.20** with Compose compiler plugin
- **Java 21** (sourceCompatibility & targetCompatibility)
- **JVM Target**: JVM 21
- **Android SDK**: compileSdk 37, targetSdk 37, minSdk 28 (Android 9+)

## Framework

- **Jetpack Compose** via BOM `androidx.compose:compose-bom:2026.06.01`
- **Material 3** (`androidx.compose.material3:material3`) with Expressive support (M3E)
- **Navigation Compose** 2.9.8
- **Lifecycle** 2.9.3 (runtime-ktx, runtime-compose)
- **Activity Compose** (latest from BOM)
- **Core KTX** 1.19.0

## Build System

- **Gradle 9+** (wrapper: `./gradlew`)
- **Android Gradle Plugin** 9.2.1
- **Build Tools & NDK**: at least API 28

## Build Configuration

Version info:
- versionCode: 1784644720
- versionName: "2.0.0-alpha"

Build types:
- debug (default)
- release (no minification yet, proguard rules exist but unused)

## Development Requirements

- OpenJDK 21
- "Global network" environment (access to Google Maven Central)
- Android build tools & NDK API 28+

## Why

Material 3 Expressive (M3E) is a key feature goal per README. Compose BOM ensures dependency version alignment. Java 21 is cutting-edge for Android (most projects use 17).
