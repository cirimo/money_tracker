# 0015. Navigation 3

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

Screen transitions, shared elements and overlays are central to the product, and the shape of
navigation (tabs, sheets, a single canvas) is a design decision that has not been made.

## Decision

`androidx.navigation3` 1.2.0 in a single activity, with `lifecycle-viewmodel-navigation3` for
per-entry view model stores. Keys are `@Serializable` and carry ids only. Results use the
library's result API. There are no deep links in the first release.

Navigation Compose 2.x was rejected: its graph and routes hide the back stack, which makes
custom transitions and unusual layouts harder.

## Consequences

- The back stack is ordinary state the app owns, so any navigation shape the design asks for
  can be built.
- Keys need kotlinx.serialization, which the app needs for export anyway.
- A deep link, when one is wanted, is mapped to a back stack by hand.
- Changing library would touch the `navigation` package and each `Route`, not the screens.
