# 0033. Correcting and deleting a record

- **Status**: accepted
- **Date**: 2026-10-07, second feature session, decided by the owner

## Context

A wrong entry could not be corrected. The owner was shown the choices drawn, in both themes, as
a private page (not stored in the repository): what stands above the pad, how deleting asks,
whether a calendar comes now, and what the list shows afterwards. The owner agreed with every
recommendation.

## Decision

- **A record opens from both lists of the latest**, on the home page and on the entry screen,
  by pressing its row.
- **Correcting is a screen of its own**, a second screen of the expense feature, not a mode of
  the entry screen. It shares the pad (amount, date, categories, note, keypad) with entry. Above
  the pad the record lies on the page as the list will show it and changes while the pad is
  used. It lives in `feature/expense` because features may not use each other and the pad
  belongs to that feature.
- **Deleting asks once, in place.** The button sits at the top, away from the thumb. Pressing it
  turns the heading into the question with two buttons; nothing covers the page. Undo after
  deleting was rejected: it needs a button that is only there for a while, which is hard to
  reach with a screen reader, and a way to write a deleted record back.
- **The date gets a calendar on the correction screen.** The arrows stay for a day or two;
  pressing the date itself opens a month in place of the categories and the keypad. This is the
  calendar [decision 0030](0030-home-page-and-own-keypad.md) left for editing. The entry screen
  keeps the arrows only.
- **A correction may not move a record into the future**, the same rule as a new record. A
  record already dated after today (written in a time zone ahead of the phone's) may keep its
  date, so that correcting its amount is not refused for a date nobody touched.
- **After a correction** the page turns back to the list the record was opened from. The record
  keeps its place, lands again and stays tilted, and the heading says "Ispravljeno." After a
  delete the heading says "Obrisano." and the rows below move up.
- **Leaving with unsaved changes discards them without a question.** The main button can be
  pressed only once something differs from what is written down.
- **Words**: the screen is "Ispravak" / "Correction", its main button is the same "Zapiši" /
  "Note it", and the delete question is answered with "Obriši" / "Delete" or "Ostavi" / "Keep".

Technical choices that follow:

- A correction rewrites the amount, category, date and note and stamps `updated_at`. The id,
  the kind and `created_at` never change, so the record keeps its place among the latest.
- A delete removes the row. There are no tombstones until sync needs them
  ([ARCHITECTURE.md](../ARCHITECTURE.md#room-for-what-comes-later)).
- Schema version 1 did not change.
- The result goes back to the list through Navigation 3's result bus. The list's view model
  keeps it as state, so only the moment between the write and the list receiving it could lose
  it, and then only the second landing is lost, never data.

## Consequences

- The pad is shared, so a change to it shows on both screens. The entry screen's rule that a
  form on "today" holds no date does not apply to a correction, which always holds one.
- Correcting an amount means taking it back key by key; there is no key that clears it.
- The calendar and its day cell are ours to maintain, including their accessibility.
- Recording an income will need the same pair of screens or a generalisation of these.
