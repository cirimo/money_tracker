# Architecture

**This document is a stub.** It will be written by the session dedicated to application
architecture. Until then the app is a single `:app` module with one placeholder screen, and no
architectural choice has been made.

If you are not in that session, do not make any of the decisions listed below as a side effect
of other work. If a task cannot proceed without one, stop and ask the owner.

## What is already fixed

These came from the bootstrap session and constrain the architecture. Each has a record in
[decisions/](decisions/README.md).

- Kotlin and Jetpack Compose, native Android only.
- Gradle with Kotlin DSL, a version catalog and convention plugins in `build-logic`, set up so
  that adding a module means applying a convention plugin and nothing else.
- Minimum SDK 26, target and compile SDK 37.
- Money is never floating point.
- All user-facing text comes from string resources, English and Croatian.
- The first release stores everything on the device, needs no account and works offline
  ([PRODUCT.md](PRODUCT.md)).

## Decisions the architecture session must make

Structure:

- The overall pattern for the UI layer and how state flows (for example unidirectional data
  flow with a specific state holder), and what the layers are.
- The module layout: whether and when to split `:app` into feature, core and data modules, the
  naming scheme, and the dependency rules between modules. The build already supports this; add
  the matching convention plugins (`trosko.android.library`, `trosko.android.feature` and so on).
- The package structure inside a module.

Libraries:

- Dependency injection: which approach, or none.
- Navigation between screens, including how arguments and results are passed, and deep links.
- Persistence: the database or storage library, the schema approach, and how migrations are
  written and tested. Migrations matter from the first public release, because users' financial
  history must survive every update.
- Asynchronous work and reactive streams: coroutines and Flow usage rules, dispatchers, scopes.
- Settings and preferences storage.
- Date and time: which API represents dates, times and time zones, and how periods such as "this
  month" are defined.
- Whether a serialisation library is needed (export, backup) and which.

Domain:

- How money is represented: integer minor units, `BigDecimal` or a dedicated value type; how
  currency is attached; the rounding rules. The hard rule is already set in
  [CONVENTIONS.md](CONVENTIONS.md#money); this decision is the concrete type.
- The core domain model: record, category, income source, period, and whether expense and income
  are one concept or two.
- Whether the first release is strictly single-currency in the data model, or only in the UI.

Data safety:

- Backup. Both `android:allowBackup` and the data extraction rules currently exclude everything,
  as a safe placeholder. Decide whether the user's data may be included in Android's cloud
  backup and device-to-device transfer, and update the manifest and the two XML rule files
  together.
- Export and import: the format, and what guarantees it gives.
- Whether data at rest is encrypted.

Quality:

- The testing strategy per layer: what is unit tested, what is tested on a device, whether
  screenshot tests are used, and how the database and migrations are tested.
- Error handling across layers, building on [CONVENTIONS.md](CONVENTIONS.md#error-handling).
- Logging and crash reporting, and what privacy rules apply to them.
- Performance guardrails for an animation-heavy app: baseline profiles, Compose stability
  rules, and how jank is measured.
- Whether to add Compose-specific static analysis rules to detekt or ktlint.

Rendering:

- Whether custom rendering needs anything beyond Compose's own drawing APIs. This overlaps with
  [DESIGN.md](DESIGN.md), and the design session's motion principles should inform it.

## When this document is written

Replace this stub entirely. The finished document should let a new session answer, without
reading the code: where does a new screen go, where does a new piece of logic go, how does data
get from storage to the screen and back, and what may depend on what. Record each decision in
[decisions/](decisions/README.md), and move the coding rules that follow from them into
[CONVENTIONS.md](CONVENTIONS.md).
