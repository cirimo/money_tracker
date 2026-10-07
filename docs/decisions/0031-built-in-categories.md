# 0031. Nine built-in expense categories with fixed ids

- **Status**: accepted
- **Date**: 2026-10-07, first feature session, approved by the owner "for now"

## Context

[Decision 0018](0018-domain-model-and-identifiers.md) says built-in categories have fixed
UUIDs and take their names from string resources. Which categories, and how they look, was
open. The owner was shown the set drawn in both themes.

## Decision

Nine expense categories, in this order: groceries (green, basket), eating out (orange, cup),
transport (cyan, bus), home (violet, house), bills (yellow, bolt), health (pink, heart), fun
(cyan, ticket), shopping (green, bag) and other (orange, dots). Six colours serve nine
categories, so colours repeat; neighbours in the grid of three never share one, and a category
is never told by colour alone.

They are listed in `BuiltinCategories` in the domain, with their ids written out, and are
inserted while the database is being created. Both timestamps of a seeded row are zero.

There are no built-in income sources yet; they come with recording an income.

The owner accepted the set as a start. The Croatian names "Vani" and "Stan" are short so that
they fit a chip, and are the ones most likely to change.

## Consequences

- An id or a key in that list must never change or be reused once a release has shipped; a
  test pins the first one as a reminder. Changing a name or a look is free until then, and a
  migration afterwards.
- A category added in a later version needs a migration that inserts it, because seeding only
  happens when a database is created.
- A key this build does not know, from a database written by a newer build, is shown as
  "Other".
