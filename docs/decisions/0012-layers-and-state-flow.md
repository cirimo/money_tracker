# 0012. Three layers and one-way state flow

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

The app has few screens but each is heavily animated, and one developer maintains it. The UI
pattern has to keep what is true apart from how it moves, and must not cost a dependency.

## Decision

Three layers: UI, domain and data. Each screen is a `UiState`, a `ViewModel` exposing one
`StateFlow<UiState>`, a stateless `Screen` and a `Route` that connects them. Events are plain
functions on the view model. One-off effects are state the UI acknowledges. Animation state
stays in composables. The details are in [ARCHITECTURE.md](../ARCHITECTURE.md#the-ui-layer).

MVI libraries (Orbit, Circuit, Molecule) were considered and rejected: each is a dependency for
a pattern that takes a few dozen lines with what AndroidX already provides.

## Consequences

- Screens are previewable and testable without a view model; view models are testable on the
  JVM with fakes.
- There is some repetition per screen (four files), accepted for being predictable.
- Changing the pattern later means touching every screen, so the cost grows with the app.
