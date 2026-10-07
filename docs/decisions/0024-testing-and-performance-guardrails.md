# 0024. Testing per layer, Compose rules and performance guardrails

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

`check` is the single gate ([decision 0010](0010-quality-tooling.md)), and 0010 left open
whether to add Compose-specific static analysis.

## Decision

- Domain logic and view models are tested on the JVM and run in `check`. The database and
  screens are tested on a device; `check` compiles those tests.
- No Robolectric and no Turbine: neither earns its place yet.
- Screenshot tests wait for the design session. The official plugin was still alpha on this
  date.
- Compose static analysis comes from compose-rules 0.6.7 as a ktlint rule set loaded by
  Spotless. Its detekt flavour needs detekt 2, which is not stable.
- A stability configuration file marks domain types as stable instead of adding an immutable
  collections library.
- A baseline profile and macrobenchmark are added with the first real screen.

## Consequences

- Database and migration tests do not run in CI until an emulator job exists. Until then they
  are run by hand whenever `:core:data` changes.
- Every type in `:core:domain` must be immutable, because the stability file promises it.
- The compose-rules checks fail `spotlessCheck`, and so `check`, on violations such as a
  composable without a modifier parameter.
