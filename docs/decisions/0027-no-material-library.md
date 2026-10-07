# 0027. No Material library; components are our own

- **Status**: accepted
- **Date**: 2026-10-07, design session, decided with the owner

## Context

[Decision 0011](0011-provisional-placeholders.md) left open whether to build on Material 3.
The design language is outline, hard shadow and press-flat.

## Decision

The app uses Compose's foundation layer only. No Material library is added. Components are
written in `:core:designsystem` on top of `StickerSurface`. This supersedes the Material item
of 0011.

Building on Material 3 and restyling it was rejected: its elevation, ripple and colour roles
would all have to be switched off or overridden, and each default that slips through is a
screen that looks like a template.

## Consequences

- Every component, including text fields, sheets and dialogs, is written here, with its own
  semantics, focus handling and touch target. That is real work and is where accessibility
  bugs will come from.
- There is no ripple; a press is shown by the shape moving.
- The APK carries no Material code.
- If a platform feature turns out to need a Material component, adding the library for that one
  case is a change to this decision.
