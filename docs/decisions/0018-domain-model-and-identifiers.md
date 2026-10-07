# 0018. One record model, categories with kinds, UUID keys

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

The schema is the most expensive thing to change once people have data in it. The features
planned for later (accounts, recurring records, several currencies, sync) must fit without a
painful migration, but must not be built now.

## Decision

- An expense and an income are one `record` with a `kind`. An income source is a category of
  kind income.
- The foreign key from record to category covers `(category_id, kind)`.
- Primary keys are UUIDs stored as text, generated on the device. Built-in categories have
  fixed UUIDs.
- A built-in category's name comes from string resources through `builtin_key` until the user
  renames it.
- A category with records is archived, not deleted. The owner chose this over reassigning or
  deleting its records.
- Enumerations are stored as text through an explicit mapping.

The schema and the table showing how each later feature fits are in
[ARCHITECTURE.md](../ARCHITECTURE.md#the-domain-model).

Two tables for expense and income were rejected because every history and balance query would
be written twice. Integer keys were rejected because ids would have to be rewritten on restore
and sync.

## Consequences

- The database refuses an expense filed under an income source.
- Text keys make indexes somewhat larger, which is irrelevant at tens of thousands of rows.
- Adding accounts with transfers will need one rebuild of the `record` table, because
  `category_id` has to become nullable. Everything else on the later list is additive.
