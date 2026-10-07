# 0032. The baseline profile gets a session of its own

- **Status**: accepted
- **Date**: 2026-10-07, first feature session, approved by the owner

## Context

[Decision 0024](0024-testing-and-performance-guardrails.md) said a baseline profile and a
macrobenchmark are added with the first real screen. Doing so needs a fifth module (a
`com.android.test` module, where the architecture has four), the `androidx.baselineprofile`
plugin and three libraries, none of which the owner had approved, and it changes nothing the
user sees.

## Decision

The first feature shipped without it. It is done in a short session of its own, where the
module, the plugin and the libraries can be approved together. This changes the timing in
0024, not its intent.

Stable versions on Google's Maven repository on this date, to be looked up again when the work
starts: `androidx.baselineprofile` 1.5.0, `benchmark-macro-junit4` 1.5.0, `profileinstaller`
1.4.1, `uiautomator` 2.4.0. Whether the plugin works with AGP 9.4.1 was not checked.

## Consequences

- Until then, startup and the first frames of the entry screen run without a profile, and
  smoothness has only been judged on a debug build, which proves nothing.
