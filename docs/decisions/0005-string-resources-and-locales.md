# 0005. All text in string resources, English and Croatian

- **Status**: accepted
- **Date**: 2026-10-07, decided by the owner before the bootstrap session

## Context

The app will ship in more than one language. Extracting hardcoded strings later is tedious and
misses cases such as content descriptions and composed sentences.

## Decision

No user-facing string is hardcoded. Everything goes through Android string resources from the
first screen. English is the default locale (`values/`) and Croatian is the second
(`values-hr/`).

## Consequences

- Every new string is added in both languages in the same change. Lint treats a missing or
  extra translation as an error and fails the build.
- The supported locales are declared in `app/build.gradle.kts` (`localeFilters`) and
  `res/resources.properties`. The locale config is generated, so the per-app language picker
  on Android 13 and newer works. Adding a language means adding its `values-*` folder and
  listing it in `localeFilters`.
- Translations shipped by libraries for other languages are stripped from the app.
- Tests must not assume a device language.
- The rules for writing strings are in [CONVENTIONS.md](../CONVENTIONS.md#strings).
