# 0021. Export as CSV and JSON, restore from JSON

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

[PRODUCT.md](../PRODUCT.md) requires a way to get the user's data out. The owner was asked
which formats, and whether the first release must be able to read its own export back.

## Decision

The owner decided: CSV for people, plus a versioned, lossless JSON file that Troško can
restore. The guarantees restore gives are listed in
[ARCHITECTURE.md](../ARCHITECTURE.md#backup-export-and-restore). The JSON is written with
kotlinx.serialization from classes separate from both the entities and the domain model.

Restore from JSON is also how the owner's friends, who will use release builds before the Play
release, move to the Play version if its signing key differs. The owner chose that route.

## Consequences

- The JSON format is a second public contract next to the database schema. Once a release
  build has written a file, every `formatVersion` must stay readable.
- CSV is output only. It uses a dot as the decimal separator, which spreadsheet programs in a
  Croatian locale may misread; a localised variant is an open product question.
- Importing from banks or other apps is a separate, later feature.
