# 0017. Money is integer minor units with a currency

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

[Decision 0004](0004-money-is-never-floating-point.md) forbids floating point for money and
left the concrete type open.

## Decision

`Money(minorUnits: Long, currency: java.util.Currency)` in `:core:domain`, stored as an
`INTEGER` column and a `TEXT` currency column. No division, overflow throws, mixing currencies
throws. Every stored amount carries its currency, although the first release offers only the
euro in the UI.

`BigDecimal` was rejected: Room would store it as text, and then a `SUM` in SQL either does not
work or silently goes through floating point, which is the very thing 0004 forbids.

## Consequences

- Totals are exact and can be computed in SQL.
- Several currencies later need no change to existing rows.
- Anything that divides money needs a new, deliberate function with a rounding mode.
- Changing this type later would touch every row, every query and the export format. Treat it
  as permanent.
