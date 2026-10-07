# Product

This is the first written version of the product definition, drafted in the bootstrap session
from a short brief. Treat the vision and the principle as settled. The feature lists are a
starting point: the owner intends to work them out further and add features over time, so
confirm the scope of a feature with the owner before building it.

## Vision

Troško helps one person understand their own money: what they spend on, where income comes from,
and how both change over time. It does this in a way people want to come back to. Most finance
apps are accurate and joyless, so people stop logging after two weeks and the data becomes
useless. Troško bets that if recording an expense feels good, people keep doing it, and the
picture of their money stays true.

The name comes from the Croatian *trošak* (expense) and *trošiti* (to spend). It reads as a
nickname, which fits an app with a character of its own.

## Who it is for

A single person tracking their own everyday money by hand. They are not an accountant and do not
want to become one. They have tried a budgeting app or a spreadsheet before and dropped it because
it felt like homework. They open the app many times a day for a few seconds each, usually right
after paying for something, often one-handed and distracted.

It is not for households that need shared ledgers, for businesses, or for people who want their
bank to do the tracking for them. Those may come later, but nothing in the first release is
shaped around them.

## The principle: fun, and not like other finance apps

Personality is the product, not decoration added once the features work. Motion, playful
interaction and a distinctive visual identity are built into each feature from the start.

"Fun" is easy to agree with and hard to apply, so here is what it means in practice. Use these to
settle arguments.

1. **Logging something ends with a small reward.** Saving an expense or an income produces a
   response the user can see and feel. If an action completes silently with a row appearing in a
   list, it is not finished.
2. **The screenshot test.** If a screenshot of a screen could be mistaken for a banking app or a
   generic template, the screen is not finished. Default components with default styling fail
   this test.
3. **Speed comes before spectacle.** Recording an everyday expense takes a few seconds and very
   few taps. No animation may make the user wait before they can do the next thing. Delight
   happens alongside the task, never in front of it.
4. **It never scolds.** Troško does not shame the user for spending, does not use red warnings as
   its default voice, and does not moralise. It is on the user's side. Observations are fine;
   judgement is not.
5. **Playful presentation, exact numbers.** The way an amount is shown can be as lively as we
   like. The amount itself is always exact, always legible, and never rounded or hidden for
   effect.
6. **Fun is for everyone.** Motion respects the system's reduced-motion setting, nothing depends
   on colour alone, and everything works with a screen reader. A playful effect that excludes
   someone is a bug. (The app has no sound or haptics at all; see [DESIGN.md](DESIGN.md).)
7. **Novelty must survive the hundredth use.** The user sees the main interactions several times
   a day. An effect that is charming once and tiring by Friday is worse than none. Prefer short,
   varied, physical-feeling responses over long set pieces.

When two of these pull against each other, the order of priority is: correct money, then speed of
entry, then accessibility, then delight, then information density. A beautiful screen that makes
logging slower loses. A dense, efficient screen with no character also loses, to principle 2.

## Features

### Must have for the first public release

- Record an expense: amount, category, date, optional note. This is the most used path in the app
  and gets the most design attention.
- Record an income the same way, with its source.
- Edit and delete any record.
- Categories for expenses and sources for income, with sensible defaults and the ability to add,
  rename and remove them.
- A history of records that can be browsed and filtered by period and category.
- An overview of a period: total spent, total earned, the balance between them, and spending
  broken down by category.
- A view of change over time: how spending and income compare from one month to the next.
- A single currency, the euro. The first market is Croatia.
- Everything stored on the device. No account, no sign-in, and the app works fully offline.
- English and Croatian, following the system language, with the per-app language setting
  supported.
- A way to get the user's data out (export), so that trusting the app with months of records is
  not a one-way door.

### Later

- Budgets and goals per category or period.
- Recurring records such as rent, salary and subscriptions.
- Several accounts or wallets (cash, card, savings) and transfers between them.
- Several currencies with conversion.
- Reminders and a home-screen widget for faster entry.
- Backup and sync across devices.
- Import from bank statements or other apps.
- Achievements, streaks and other long-term play, if they can be done without breaking
  principle 4.
- Shared ledgers for households.

### Not planned

- Connecting directly to bank accounts.
- Advertising, or selling or sharing user data.
- Investment tracking, tax reporting or anything that needs financial advice.

## Open product questions

These are tracked in [STATUS.md](STATUS.md) until the owner answers them. Answers get recorded
here and, where they are decisions, in [decisions/](decisions/README.md).
