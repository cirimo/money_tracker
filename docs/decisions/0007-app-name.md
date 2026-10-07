# 0007. The app is called Troško

- **Status**: accepted
- **Date**: 2026-10-07, decided by the owner in the bootstrap session

## Context

The project needed a name for the app label, the Gradle project and the package.

## Decision

The app is named **Troško**, from the Croatian *trošak* (expense) and *trošiti* (to spend). It
reads as a nickname, which suits an app meant to have a character.

## Consequences

- The user-visible name is the string resource `app_name`, written with the diacritic and
  marked as not translatable.
- Identifiers use the ASCII form `trosko`: the Gradle root project, the package, the theme
  name and the convention plugin ids.
- The owner confirmed the name as final in the same session. The application id is derived
  from it and is permanent; see [0008](0008-application-id.md).
- Trademark and Play Store name availability have not been checked. That is on the list in
  [RELEASE.md](../RELEASE.md).
