# 0004. Money is never floating point

- **Status**: accepted
- **Date**: 2026-10-07, decided by the owner before the bootstrap session

## Context

Binary floating point cannot represent most decimal fractions exactly. Sums drift, equality
checks fail, and totals come out a cent wrong. In a finance app, one wrong total is enough to
lose the user's trust permanently.

## Decision

Money is never stored, passed or computed as `Float` or `Double`, not even as an intermediate
value. This is a hard rule with no exceptions for convenience.

## Consequences

- Amounts need an exact representation. Which one (integer minor units, `BigDecimal`, a value
  type) is left to the architecture session and listed in
  [ARCHITECTURE.md](../ARCHITECTURE.md).
- Parsing user input, arithmetic, rounding and formatting all need care. The practical rules
  are in [CONVENTIONS.md](../CONVENTIONS.md#money).
- Floating point remains acceptable for things that are not money: animation progress and
  chart geometry. Such values never flow back into an amount.
- Nothing enforces this automatically yet. A custom lint or detekt rule is worth considering
  once a money type exists.
