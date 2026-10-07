# 0030. A home page first; entry is its own screen with the app's own keypad

- **Status**: accepted
- **Date**: 2026-10-07, first feature session, decided by the owner

## Context

[ARCHITECTURE.md](../ARCHITECTURE.md) left the shape of navigation open until the first
feature. The owner was shown three layouts of the entry screen as a working page, in both
themes: entry on top with records under the button, the page of records on top with a pad for
entry below, and the same screen with the system keyboard.

## Decision

- **The app starts on a home page, not on entry.** The home page will hold statistics. Until an
  overview exists it shows the latest entries and a button, low on the screen, that turns the
  page to a new expense. The recommendation had been to start on entry; the owner chose
  otherwise because the app will do more than record.
- **Recording an expense is a screen**, laid out as the second variant: the page where records
  land on top, and below it the amount, date, categories, note and keypad, with the main button
  as a tall key of the keypad.
- **The amount is typed on the app's own keypad.** The decimal key of a system numeric keyboard
  depends on the keyboard and the language and is missing on some; the owner's phone runs
  Samsung's keyboard in Croatian. The note uses the system keyboard.
- **The date changes one day at a time** with two arrows, never into the future. A calendar
  waits for editing.
- **Zero cannot be saved.** The button stays disabled until there is an amount above zero and a
  category. The keypad stops at seven whole digits.
- **After a save** the record lands at the top of "latest entries", which is ordered by when a
  record was written and not by its date, so a record dated yesterday lands on top too. The
  form empties completely, the date included.
- **The amount sticker takes the colour of the chosen category.**

## Consequences

- Recording an expense costs one tap more than it would from a start on entry. A shortcut or a
  widget is the place to win that back ([PRODUCT.md](../PRODUCT.md), later features).
- A wrong entry cannot be corrected yet; editing and deleting are a later feature.
- The keypad, its keys and their accessibility are ours to maintain.
