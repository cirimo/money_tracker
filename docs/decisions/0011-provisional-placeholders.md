# 0011. Provisional placeholders awaiting the architecture and design sessions

- **Status**: accepted, and expected to be superseded piece by piece
- **Date**: 2026-10-07, bootstrap session

## Context

The bootstrap session was told not to make architecture or design decisions, but a buildable,
lint-clean app needs a few things to have some value. Each of these was given the most neutral
value available and is recorded here so that nobody mistakes it for a choice.

## Decision

- **Backup is switched off.** `android:allowBackup="false"`, and both backup rule files exclude
  everything. Lint requires the attributes to be explicit, and excluding everything is the safe
  default for financial data. The real decision belongs to the architecture session.
- **No Material library.** The placeholder screen uses Compose foundation's `BasicText` and the
  platform's `Theme.Material.Light.NoActionBar` window theme. Whether to build on Material 3 is
  a design decision.
- **The launcher icon is a black disc on white.** The real icon comes from the design session.
- **Version is `0.1.0`, version code 1**, set by hand. The scheme is undecided; see
  [RELEASE.md](../RELEASE.md).
- **The only tests are smoke tests**: one unit test and one Compose UI test that prove the
  pipelines run.

## Consequences

- None of the above is a precedent. Replace each when its owning session decides, and note it
  in that session's own decision records.
- The debug application id suffix `.debug` is not provisional; see
  [0008](0008-application-id.md).
