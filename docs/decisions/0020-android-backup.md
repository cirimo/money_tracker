# 0020. Android backup: device transfer always, cloud only when encrypted

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

[Decision 0011](0011-provisional-placeholders.md) switched backup off as a placeholder. The
real question is the owner's: may financial data leave the device through Android's own backup.

## Decision

The owner decided:

- Device-to-device transfer includes the database and the settings.
- Cloud backup includes the database only when the backup is end-to-end encrypted, which
  requires a screen lock. Without one, nothing is uploaded.

`android:allowBackup` is true, and the two rule files express the limits. This supersedes the
backup item of 0011.

## Not verified

The rules pass Lint and build. A backup and restore round trip has not been observed: the
emulator's local test transport refused the package, and the reason was not established. It
must be tested on a real phone before release, including whether the database's `-wal`, `-shm`
and `.lck` companion files travel correctly.

## Consequences

- A lost or replaced phone does not mean lost history, for users with a screen lock.
- The Play data safety form and the privacy policy must describe this.
- A restore can deliver a database older or newer than the installed app; the migration policy
  in [ARCHITECTURE.md](../ARCHITECTURE.md#migrations) covers both.
