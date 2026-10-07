# 0001. Native Android with Kotlin and Jetpack Compose

- **Status**: accepted
- **Date**: 2026-10-07, decided by the owner before the bootstrap session

## Context

The product depends on custom animation, playful interaction and a distinctive visual identity.
It targets Android only and is headed for Google Play.

## Decision

The app is written natively in Kotlin, with Jetpack Compose for the whole UI.

## Consequences

- We get the most direct control over rendering, animation and input that the platform offers,
  which is what the product needs most.
- There is no iOS or web version, and no code is shared with one. A future port would be a
  separate effort.
- No XML layouts or View-based screens are added. Interop with Views is acceptable only when a
  platform feature has no Compose equivalent.
- Cross-platform frameworks are not to be proposed as alternatives.
