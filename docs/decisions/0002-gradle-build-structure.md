# 0002. Gradle Kotlin DSL, version catalog and convention plugins

- **Status**: accepted
- **Date**: 2026-10-07, decided by the owner before the bootstrap session

## Context

The project starts as one module, but the architecture session may split it into several.
Build configuration copied between modules drifts and makes every upgrade a multi-file edit.

## Decision

- Gradle with the Kotlin DSL throughout.
- Every version and dependency coordinate lives in `gradle/libs.versions.toml`.
- Shared build configuration lives in convention plugins in the included build
  `build-logic/convention`: `trosko.android.application`, `trosko.android.compose`,
  `trosko.detekt` and `trosko.spotless`.
- A module's own `build.gradle.kts` contains only what is specific to that module.

## Consequences

- Adding a module means applying convention plugins, with no build refactoring. A library module
  will need a `trosko.android.library` plugin, to be written when the first one is added.
- Convention plugins cannot use the generated `libs` accessors and look entries up by alias
  through the helpers in `VersionCatalog.kt`.
- Plugins are declared `apply false` in the root build so that all modules share one version,
  and `build-logic` depends on them as `compileOnly`.
- A version written anywhere but the catalog is a mistake.
