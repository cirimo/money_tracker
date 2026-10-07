# Architecture

How Troško is put together, and where new code goes. Each choice here has a record in
[decisions/](decisions/README.md) that gives the alternatives and what it would cost to change;
this document states the result. The coding rules that follow from it are in
[CONVENTIONS.md](CONVENTIONS.md).

Some of what is described is a rule for code that does not exist yet (settings, export, the
record model, migrations). [STATUS.md](STATUS.md) says what has actually been built.

## The short version

- **A new screen** goes in `:app`, in its own package `feature/<name>/`, as a `Route`, a
  `Screen`, a `ViewModel`, a `UiState` and a navigation key.
- **A new piece of logic** (anything about money, dates, rules or calculations) goes in
  `:core:domain`, which is plain Kotlin, and gets a JVM unit test.
- **Data travels** from a Room DAO as a `Flow`, through a repository that turns rows into
  domain types, into a view model that exposes one `StateFlow<UiState>`, to a `Route` that
  collects it and a `Screen` that draws it. Back the other way: a lambda on the screen calls a
  function on the view model, the domain validates, the repository writes in one transaction,
  and Room re-emits to every observer. Nothing refreshes by hand.
- **Dependencies point inwards**: `:app` may use everything, `:core:data` may use
  `:core:domain`, and `:core:domain` and `:core:designsystem` use none of our modules.

## Modules

| Module | Kind | Holds | May depend on |
|---|---|---|---|
| `:app` | Android application | `TroskoApplication`, `AppContainer`, `MainActivity`, navigation, every `feature/*` package, formatting of amounts and dates | all three `:core` modules |
| `:core:domain` | plain Kotlin (JVM) | `Money`, the model, periods, rules, calculations, repository interfaces | nothing of ours |
| `:core:data` | Android library | the Room database, settings storage, export and restore, repository implementations | `:core:domain` |
| `:core:designsystem` | Android library with Compose | theme, tokens, fonts, components, motion | nothing of ours |

The boundaries that are most expensive to break are the ones the compiler enforces. `Money`
cannot touch Compose or Room, because `:core:domain` has neither on its classpath. A screen
cannot run a query, because the database classes are `internal` to `:core:data` and its only
public door is the `Repositories` class.

`:core:designsystem` does not depend on `:core:domain`. Its components take text and plain
values, not `Money` or `Category`, so the design system stays a library of looks and motion and
the feature decides what the words and numbers are.

Features are packages inside `:app`, not modules. One feature must not import from another;
they meet only in the `navigation` package. The compiler does not enforce this, so it is a rule
to keep. Extract a feature into a `:feature:<name>` module when something outside the app needs
it (a widget, for example) or when build times ask for it.

A module's `build.gradle.kts` applies convention plugins and lists its own dependencies, nothing
else. The plugins are `trosko.android.application`, `trosko.android.library`,
`trosko.jvm.library`, `trosko.android.compose`, `trosko.android.room` and
`trosko.kotlin.serialization`.

### Packages

```
:app                 dev.cirimo.trosko
  (root)             TroskoApplication, AppContainer, MainActivity
  navigation/        TroskoNavDisplay: the back stack and the key-to-route mapping
  feature/<name>/    XKey, XRoute, XScreen, XViewModel, XUiState, and the feature's own pieces;
                     a feature may hold more than one screen (expense: entry and correction)
  format/            turning Money, dates, categories and records into the words, colours
                     and rows the current language and theme call for; it is also where
                     what two features both show (a record as a row) lives, because
                     features may not use each other

:core:domain         dev.cirimo.trosko.domain
  money/             Money and everything that computes with it
  model/             Record, Category, RecordKind, ids
  period/            Period and the calendar rules
  repository/        the interfaces :core:data implements

:core:data           dev.cirimo.trosko.data
  (root)             Repositories, the module's only public class
  db/                entities, DAOs, the database, migrations (all internal)
  repository/        repository implementations (internal)
  settings/          settings storage (internal)
  transfer/          export and restore (internal)

:core:designsystem   dev.cirimo.trosko.designsystem
  theme/             TroskoTheme and its tokens: colours, type, shapes, sizes, motion
  component/         StickerSurface and everything built on it
```

## The UI layer

State flows one way. Each screen has four parts:

- **`XUiState`** is an immutable value, a data class or a sealed interface, that says everything
  the screen shows. Amounts and dates arrive already formatted or as domain values ready to
  format; the screen never computes.
- **`XViewModel`** exposes exactly one `StateFlow<XUiState>`, built from repository flows with
  `stateIn`, and plain functions for what the user can do (`onAmountChanged`, `onSave`). It holds
  what is true. It does not hold animation state.
- **`XScreen`** is a stateless composable that takes the state and lambdas. It has a preview and
  can be tested without a view model.
- **`XRoute`** is the only composable that knows the view model exists. It builds it, collects
  the state with `collectAsStateWithLifecycle` and calls the screen.

A one-off effect, such as the reward after a record is saved, is modelled as state that the UI
acknowledges, not as an event on a channel. An event can be lost across a rotation or a process
death; state cannot.

How something moves is the composable's business and lives in the composable. The view model
says a record was saved; the screen decides what that looks and feels like.

## Dependency injection

There is no framework. Every class takes what it needs through its constructor, and one class,
`AppContainer` in `:app`, creates the long-lived objects and wires them together. It is owned by
`TroskoApplication` and created lazily.

A route builds its view model with `viewModel { XViewModel(container.repositories.x) }`, taking
the container from `appContainer()`. Tests construct the view model directly with hand-written
fakes of the repository interfaces.

## Navigation

Navigation 3, in a single activity. The back stack is a list of keys that the app owns
(`rememberNavBackStack`), and `TroskoNavDisplay` maps each key to its route.

- A key is a `@Serializable` object or data class implementing `NavKey`, declared in its
  feature's package. It carries ids and small values only, never domain objects: the screen
  loads what it needs from a repository, so it is correct after process death too.
- Every entry gets its own view model store, cleared when the entry leaves the back stack.
- A result from one screen to another uses Navigation 3's result API, not a shared view model.
  The sending route calls `sendResult` on `LocalResultEventBus`, the receiving route collects it
  with `ResultEffect` and hands it to its view model, which keeps it as state. The value sent
  lives in `format/` when two features must both know it, as `RecordChange` does.
- There are no deep links, and the manifest has no intent filter besides the launcher. When a
  widget or shortcut needs one, the activity turns the intent into a back stack by hand.

Which screens exist, whether there are tabs, and what is a sheet or an overlay are design
decisions. Navigation 3 was chosen partly because it does not force any of those shapes.

## Money

`Money` in `:core:domain` is a whole number of the currency's smallest unit (`Long`) together
with a `java.util.Currency`. Nothing else may represent an amount.

- Adding, subtracting and comparing require the same currency and throw otherwise, because
  mixing currencies is a programming error, not a user error.
- Arithmetic that would overflow throws instead of wrapping around.
- Multiplication is by whole numbers only, and there is no division. The first release only sums
  and subtracts, so nothing rounds. If a feature needs an average or a split, add one function
  that takes an explicit `RoundingMode` and decide the mode on purpose.
- Text typed by the user is parsed straight into minor units, never through `Double`, and the
  parser returns a value that says what was wrong instead of throwing.
- For display, `Money.toBigDecimal()` feeds a locale-aware `NumberFormat` in `:app`. Ratios for
  charts may be floating point, as [CONVENTIONS.md](CONVENTIONS.md#money) allows.
- In the database an amount is two columns: `amount_minor INTEGER` and `currency TEXT`. Sums are
  done in SQL on the integer column, and every aggregate query groups by currency so that a sum
  can never mix two.

The first release offers only the euro, but that is a limit of the UI. Every stored amount
carries its currency.

## The domain model

An expense and an income are one concept, a **record**, told apart by its kind. They have the
same shape, share one history, and the balance is one query over one table. What a record is
filed under is a **category**; an income source is a category of kind income, and "source" is
only the word the UI uses.

```
category                            record
  id            TEXT PK  (UUID)       id            TEXT PK  (UUID)
  kind          TEXT                  kind          TEXT     EXPENSE | INCOME
  builtin_key   TEXT NULL             amount_minor  INTEGER  always positive
  custom_name   TEXT NULL             currency      TEXT     ISO 4217
  colour        TEXT                  category_id   TEXT
  icon          TEXT                  occurred_on   TEXT     ISO 8601 date
  sort_order    INTEGER               note          TEXT NULL
  archived_at   INTEGER NULL          created_at    INTEGER
  created_at    INTEGER               updated_at    INTEGER
  updated_at    INTEGER               FOREIGN KEY (category_id, kind)
  UNIQUE (id, kind)                     REFERENCES category (id, kind)  RESTRICT
```

- **Identifiers are UUIDs** generated on the device and stored as text. They survive export,
  restore and any later sync without being rewritten, and the categories that ship with the app
  have fixed UUIDs so that two devices agree on them.
- **The foreign key covers the kind as well as the id**, so the database itself refuses an
  expense filed under an income source.
- **The amount is always positive**; the sign follows from the kind. Room cannot declare a
  `CHECK` constraint, so this is enforced by the domain (`NewRecord` cannot be made with any
  other amount), not by the database.
- **A category that ships with the app has no stored name.** `builtin_key` selects a string
  resource, so the name follows the app language. Once the user renames it, `custom_name` holds
  what they typed and wins.
- **A category with records is archived, never deleted.** It stops being offered for new
  records and old ones keep it. A category with no records is really deleted. Records are really
  deleted.
- **Correcting a record** rewrites its amount, category, date and note and stamps `updated_at`.
  Its id, kind and `created_at` never change
  ([decision 0033](decisions/0033-correcting-and-deleting-a-record.md)).
- **Enumerations are stored as text through an explicit mapping** (`RecordKindColumn.kt`), never
  by the enum's name, so renaming a constant in Kotlin cannot change what is in users' databases.
- **Timestamps** (`created_at`, `updated_at`, `archived_at`) are milliseconds since the epoch in
  UTC. `created_at` also orders records within one day.

A category's look is one of the six sticker colours and one icon
([DESIGN.md](DESIGN.md#colour)), each stored as a text key mapped explicitly like `kind`. The
domain holds them as names (`CategoryColour`, `CategoryIcon`); `:app` turns a name into a
colour of the theme and a drawing of the design system.

The categories that ship with the app are listed in `BuiltinCategories` in the domain and
written into the database as part of creating it. Their timestamps are zero, so that when a
restore compares `updated_at`, anything the user has changed wins over the seed
([decision 0031](decisions/0031-built-in-categories.md)).

### Room for what comes later

Nothing from the "Later" list in [PRODUCT.md](PRODUCT.md) is built or stubbed. This is how each
would fit, so that nobody closes the door by accident.

| Later feature | How the model takes it | Migration |
|---|---|---|
| Budgets and goals | a new `budget` table pointing at a category | `CREATE TABLE` |
| Recurring records | a new rule table, and a nullable `recurring_rule_id` on `record` | `CREATE TABLE`, `ADD COLUMN` |
| Accounts and transfers | a new `account` table, `account_id` on `record` filled with a default account, and a transfer as a third kind | a rebuild of the `record` table, because `category_id` has to become nullable. The one non-trivial migration on this list. |
| Several currencies | `currency` is already on every record; add a rate table or a converted amount | `ADD COLUMN` or `CREATE TABLE` |
| Sync | ids are already UUIDs and `updated_at` exists; add `deleted_at` tombstones | `ADD COLUMN` |
| Import from banks | goes through the same domain validation; perhaps an `external_id` | `ADD COLUMN` |
| Widget, reminders | no schema; repositories are already reachable outside an activity | none |
| Streaks, achievements | derived from records, or their own table | none, or `CREATE TABLE` |
| Shared ledgers | needs sync and a `ledger_id`; the largest change, and only the UUIDs prepare for it | substantial |

## Dates and periods

The API is `java.time`; minimum SDK 26 has it natively.

- A record's date is a `LocalDate`: the calendar day the user means. It has no time zone, so an
  expense on 5 October stays on 5 October wherever the phone travels. It is stored as ISO 8601
  text, which sorts correctly and reads plainly.
- A `Period` is a half-open range of dates, from a start day up to but not including an end
  day. "This month" is the calendar month that contains today in the device's time zone. Month
  over month is a sequence of such periods.
- Code never asks the system for the current time directly. A `java.time.Clock` is passed in,
  so tests can fix the date. The app has one, `DeviceClock`, created in `AppContainer`; it
  reads the time zone on every call, so "today" stays right when the phone travels.
- A form that means "today" holds no date at all until it is saved, so one left open over
  midnight is filed under the day it was saved on.
- A month that starts on payday, if it is ever wanted, is a setting that changes how periods are
  computed. It needs no migration.

## Persistence

Room 3 with KSP, on the bundled SQLite driver. The bundled driver ships its own SQLite, so the
engine is the same on Android 8 as on Android 17, where the platform's own differs by years.

- Reads are `Flow`, writes are `suspend`. A write that touches more than one row runs in one
  transaction.
- Entities hold plain SQLite types. The repository turns them into domain types, in one visible
  place per table, and no entity or DAO leaves `:core:data`.
- The schema of every version is exported to `core/data/schemas/` and committed.

### Migrations

Users' financial history has to survive every update for years, so:

- **Version 1 is a draft** and may change in place until the first release build is installed
  on a real phone, the owner's or a friend's. That install freezes it. Record the freeze in
  [STATUS.md](STATUS.md) when it happens.
- After the freeze, every schema change is a new version with a **hand-written migration**. No
  automatic migrations: the SQL that touches people's money is written and reviewed by hand.
- Every migration has a test from the version before it, and one test runs the whole chain from
  version 1 with realistic data and checks that the totals are unchanged.
- A release build never falls back to wiping the database. A debuggable build may, because the
  debug app is a separate installation whose data is disposable; `AppContainer` passes that flag.
- Before a migration runs, the database file is copied aside, and the copy is kept until the
  next successful start.
- If the database on disk is newer than the app (a restore onto an older build), the app says
  so and stops. It does not delete anything.

## Coroutines

- Repositories expose `Flow` for reading and `suspend` functions for writing. View models turn
  flows into state with `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)`.
- Saving a record must not be cancelled because the user left the screen: the write runs in an
  application-wide scope, created in `AppContainer` and handed to `Repositories`, and the view
  model only awaits it.
- Dispatchers are never named at the call site. Room moves queries off the main thread itself;
  anything else that needs a dispatcher receives it through its constructor, so tests can
  replace it.

## Settings

Settings are stored with DataStore Preferences in `:core:data`, behind an interface in
`:core:domain`. The first release may turn out to have none: the theme follows the system and
there is no sound or haptics to switch off. Add the storage when the first real setting appears. They are kept out of the database on
purpose: they describe a device, not the user's finances, and do not belong in an export.

The app language is not a setting of ours. Android's per-app language picker handles it.

## Backup, export and restore

**Android backup is on, with limits.** Device-to-device transfer carries the database and the
settings. Cloud backup carries the database only when the backup is end-to-end encrypted, which
needs a screen lock; otherwise nothing is uploaded. The manifest's `android:allowBackup` and the
two files `res/xml/data_extraction_rules.xml` and `res/xml/full_backup_content.xml` express
this and must be changed together.

**Export** goes through the system file picker, so it needs no permission.

- **CSV** is for people and spreadsheets, and is output only: UTF-8, ISO dates, the currency in
  its own column. It follows the app language so that a spreadsheet program opens it correctly:
  a decimal comma and semicolon-separated fields in Croatian, a dot and commas in English.
- **JSON** is lossless and is what restore reads. It carries a `formatVersion`. Its shape is
  defined by its own serialisable classes in `transfer/`, separate from the entities and from
  the domain model, so the database can change without changing the file format.

**Restore** reads a JSON export made by Troško. It guarantees:

- the whole file is validated before anything is written;
- the write is one transaction, so the result is everything or nothing, and the user is told
  which;
- rows are matched by id, existing rows are replaced only by a newer `updated_at`, and nothing
  is ever deleted;
- every older `formatVersion` stays readable forever.

A test exports, wipes and restores, and requires the data to be identical.

**There is no encryption of our own.** The app's private storage is protected by Android's
sandbox and the device's encryption.

**The app cannot reach the network.** It does not request the `INTERNET` permission, and
`RequestedPermissionsTest` checks the installed app for it, because a library can add the
permission through manifest merging without anyone noticing. There is no crash reporting SDK and
no analytics; crashes are seen through Android vitals in the Play Console.

## Errors

This builds on [CONVENTIONS.md](CONVENTIONS.md#error-handling).

- Validation in the domain returns a value that says what is wrong. It does not throw.
- A repository write catches the database's own exception type and nothing wider, and returns
  whether the record was saved. Because writes are transactional, "not saved" means untouched.
- The view model turns a failure into state, and the screen shows a message from string
  resources.
- Anything unexpected is allowed to crash. There is no catch-all.
- There is no logging library. A debuggable build may log constant messages. Nothing the user
  entered is ever logged.

## Testing

| What | Where | Runs in `check` |
|---|---|---|
| Domain: money, parsing, periods, rules | JVM unit tests in `:core:domain` | yes |
| View models, formatting | JVM unit tests in `:app` with hand-written fake repositories | yes |
| Database: DAOs, constraints, repositories, migrations | instrumented tests in `:core:data` | compiled only |
| Screens: the paths that matter, above all recording an expense | Compose instrumented tests in `:app` | compiled only |
| Components whose behaviour is motion or measurement | Compose instrumented tests in `:core:designsystem` | compiled only |

Database tests need a device because Room's Android artifact needs an Android runtime. Run
`connectedDebugAndroidTest` whenever `:core:data` changes.

Screen tests run a real view model on fake repositories, so they never write to the installed
app's database. `AppLaunchTest` is the one test that opens the real database, and it only
reads. Formatting is tested twice on purpose: on the JVM, and again on a device, because the
two format with different locale data.

Screenshot tests are not set up. They wait for the design session, so there is something worth
pinning down; choosing the tool is part of that step.

## Performance

The app animates a lot, so these hold from the first screen:

- `UiState` and everything in it is immutable. `config/compose/stability.conf` tells the Compose
  compiler that the domain module's types and `java.time` are stable, which is a promise: every
  type in `:core:domain` must be immutable.
- A value that changes on every frame is read where it is used, in a `graphicsLayer {}` or draw
  lambda, not in composition.
- Smoothness is judged on a release build. Debug builds of Compose are slow and prove nothing.
- A baseline profile and a macrobenchmark of the record-entry path are not in the build yet.
  They need a fifth module, the `androidx.baselineprofile` plugin and three libraries, so they
  get a session of their own
  ([decision 0032](decisions/0032-baseline-profile-in-its-own-session.md)).

Static checks for Compose come from the compose-rules ktlint rule set, run through Spotless.

## Where architecture meets design

[DESIGN.md](DESIGN.md) defines the look and motion; these are the points where it constrains
the code.

- **No UI library.** The app is built on Compose's foundation layer, and every component lives
  in `:core:designsystem` ([decision 0027](decisions/0027-no-material-library.md)). Features
  use those components and nothing else.
- **No chart, animation or icon library.** Charts, icons and the mascot are drawn with
  Compose's own drawing APIs. The look is flat fills and solid shapes, so nothing needs blur or
  shaders, which keeps the frame budget easy to hold.
- **The theme is `TroskoTheme`**, with colours, type and shapes provided through composition
  locals and sizes and motion as plain objects. It is the only place that may define a
  composition local.
- **Fonts are bundled**, because the app has no network access.
- **Navigation starts on a home page**, and recording an expense is a screen of its own reached
  from it ([decision 0030](decisions/0030-home-page-and-own-keypad.md)). Pressing a record on
  either opens the screen that corrects or deletes it
  ([decision 0033](decisions/0033-correcting-and-deleting-a-record.md)). Which further screens
  exist is decided feature by feature. The transition between screens is a page turn.
