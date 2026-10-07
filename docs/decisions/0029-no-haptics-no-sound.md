# 0029. No haptics and no sound

- **Status**: accepted
- **Date**: 2026-10-07, design session, decided with the owner

## Context

[PRODUCT.md](../PRODUCT.md) says a saved record ends with a response the user can see and
feel, and that sound and haptics can be turned off. The owner was asked whether the app should
vibrate or make sound at all.

## Decision

The owner decided neither. The app does not vibrate and makes no sound, and has no setting for
either.

## Consequences

- All feedback is visual, so the press and the landing sticker carry the whole reward and must
  be unmistakable.
- No audio files, no vibration permission, and nothing to respect about silent mode.
- PRODUCT.md's wording about turning sound and haptics off no longer applies; it is corrected
  there.
- Adding haptics later is cheap and touches only the design system's components.
