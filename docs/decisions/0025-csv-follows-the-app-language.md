# 0025. The CSV export follows the app language

- **Status**: accepted
- **Date**: 2026-10-07, decided by the owner after the architecture session

## Context

[Decision 0021](0021-export-and-restore.md) planned a CSV with a dot as the decimal separator
and left open what to do about spreadsheet programs set to Croatian, which expect a decimal
comma and read "12.50" as text or as a date.

## Decision

The owner decided the CSV is written with a decimal comma for Croatian. The CSV therefore
follows the app language:

- In Croatian, amounts use a decimal comma and fields are separated by semicolons, which is
  what a spreadsheet program set to Croatian opens correctly.
- In English, amounts use a dot and fields are separated by commas.

Dates stay ISO 8601 in both, and there are no grouping separators in amounts. This refines
0021; everything else in it stands.

## Consequences

- The CSV is for people and is not a stable machine format. Restore never reads it; the JSON
  export is the lossless one.
- The writer must quote any field that contains its separator, which matters for notes.
- The amount text is still produced from the exact value, never through floating point.
