# 0019. Dates are local dates, periods are half-open ranges

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

The app records when money was spent and summarises by period. Minimum SDK 26 provides
`java.time` natively.

## Decision

- `java.time`, with no date library added.
- A record's date is a `LocalDate`, stored as ISO 8601 text. The owner chose text over an
  integer day count.
- A period is a half-open range of dates. "This month" is the calendar month containing today
  in the device's time zone.
- The current time always comes from an injected `Clock`.

## Consequences

- A record keeps its calendar day when the phone changes time zone.
- ISO text sorts correctly and reads plainly in the database and in exports.
- A custom month start (payday) would be a setting that changes how periods are computed, with
  no migration.
