# 0010. Spotless with ktlint, detekt, strict Lint, one check command

- **Status**: accepted
- **Date**: 2026-10-07, bootstrap session

## Context

The owner asked for formatting and static analysis for Kotlin, Android Lint with warnings taken
seriously, and a single command that runs every local check. Sessions start without memory, so
the checks have to carry the standards that a long-lived team would carry in its head.

## Decision

- **Formatting**: ktlint 1.8.0, run through the Spotless Gradle plugin, applied once at the
  root and covering every `.kt` and `.gradle.kts` file including `build-logic`. Style is
  `ktlint_official` with a 120 column limit, configured in `.editorconfig`.
- **Static analysis**: detekt 1.23.8 with its default rule set and a few overrides for Compose
  in `config/detekt/detekt.yml`. detekt's formatting rules are not used.
- **Android Lint**: warnings are errors and abort the build. Three checks that fire whenever a
  newer dependency is published are disabled, because they would fail builds that changed
  nothing.
- **Kotlin compiler**: all warnings are errors.
- **One command**: `gradlew check` runs all of the above plus the unit tests and compiles the
  instrumented tests. CI runs the same command.

Spotless was chosen over the ktlint Gradle plugin because it takes explicit file targets and so
does not depend on how the Kotlin plugin is applied, which changed with AGP 9's built-in Kotlin.

## Consequences

- A failing check is fixed at its cause. Disabling a rule, adding a baseline or loosening a
  threshold is a change to this decision and needs the owner's agreement.
- There is no Lint baseline and no detekt baseline, and there should not be one: the project
  starts clean.
- `gradlew spotlessApply` fixes formatting; nobody formats by hand.
- Deprecations in dependencies surface as build failures. That is intended, and occasionally
  means a small migration when a library is upgraded.
- Instrumented tests are not part of `check` or CI, since they need a device. Adding an
  emulator job to CI is a possible later improvement.
- Compose-specific lint rule sets for detekt or ktlint were not added. Whether to add them is
  listed in [ARCHITECTURE.md](../ARCHITECTURE.md).
