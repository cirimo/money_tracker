# 0016. Room 3 on bundled SQLite, with hand-written migrations

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

The database holds users' financial history and must survive every update for years. Versions
were read from Google's Maven repository on 2026-10-07: Room 3.0.3 (stable since July 2026, a
new `androidx.room3` package, KSP only, coroutine based), Room 2.8.5, KSP 2.3.12,
`sqlite-bundled` 2.7.1.

## Decision

- Room 3.0.3 with KSP 2.3.12 and the bundled SQLite driver.
- Schemas are exported and committed. Version 1 is a draft until the first release build is
  installed on a real phone; from then on every change is a hand-written, tested migration.
  A release build never falls back to a destructive migration; a debuggable build may.
- The full policy is in [ARCHITECTURE.md](../ARCHITECTURE.md#migrations).

Room 2.8 was rejected because starting on it means a package migration later. SQLDelight was
rejected because Room's schema export and migration test helper are exactly the tools this
project needs most. The platform's own SQLite driver was the close alternative to the bundled
one: it adds nothing to the APK, but its SQLite version differs by years between Android 8 and
Android 17.

## Verified and not verified

- KSP 2.3.12 is not documented as tested with Kotlin 2.4.20. It was verified empirically: KSP
  and Room generate code, and `check`, debug and release builds pass.
- A composite foreign key onto a unique index works in Room 3; `TroskoDatabaseTest` proves it.
- Room's Android artifact cannot run in JVM unit tests, so database tests are instrumented and
  are not part of `check`.
- The bundled driver adds the native library `libsqliteJni.so`. Its size cost and its 16 KB
  page alignment have not been checked; that is on the list in [RELEASE.md](../RELEASE.md).

## Consequences

- The same SQLite everywhere, at the price of a native library in the APK.
- Changing the persistence library after release is very expensive. Changing the driver is one
  line, since the file format is the same.
