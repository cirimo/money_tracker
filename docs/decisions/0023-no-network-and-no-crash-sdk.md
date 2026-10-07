# 0023. No network access, no crash reporting SDK

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

The first release stores everything on the device. The owner was asked whether to add crash
reporting or analytics.

## Decision

The owner decided to rely on Android vitals in the Play Console only. The app has no crash
reporting SDK, no analytics, and does not request the `INTERNET` permission. An instrumented
test, `RequestedPermissionsTest`, fails if any dependency adds that permission through manifest
merging.

## Consequences

- "Your data does not leave this app" is true and checkable, apart from Android's own backup
  ([0020](0020-android-backup.md)), and the Play data safety form stays simple.
- Crash information is limited to what Play collects from users who opted in.
- Adding any library that needs the network is a change to this decision, not a routine
  dependency.
