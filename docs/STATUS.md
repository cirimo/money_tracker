# Status

The handoff between sessions. Read this first; rewrite it last. It describes the present, so
replace what is stale instead of appending. History lives in git.

Last updated: 2026-10-07, at the end of session 2 (architecture).

## Current state

The architecture is decided, written down and built as a skeleton. There are no features yet,
and no design: the app still shows one unstyled placeholder screen.

- [ARCHITECTURE.md](ARCHITECTURE.md) is complete, with decision records 0012 to 0025.
- Four modules: `:app`, `:core:domain`, `:core:data`, `:core:designsystem`.
- Toolchain unchanged: Gradle 9.8.0, AGP 9.4.1, Kotlin 2.4.20, Compose BOM 2026.09.00
  ([decisions/0003](decisions/0003-toolchain-versions.md)). Added: KSP 2.3.12, Room 3.0.3,
  bundled SQLite 2.7.1, Navigation 3 1.2.0, Lifecycle 2.11.0, coroutines 1.11.0,
  kotlinx.serialization 1.11.0, compose-rules 0.6.7 through Spotless.
- Quality gate and CI are unchanged: `.\gradlew.bat check`.

What the skeleton contains:

- `:core:domain`: `Money` with tests, `RecordKind`, `Category`, `CategoryId`, `CategoryName`
  and the `CategoryRepository` interface with one read method.
- `:core:data`: the Room database with the `category` and `record` tables (schema version 1,
  exported to `core/data/schemas/`), their DAOs, `RoomCategoryRepository` and the public
  `Repositories` class.
- `:core:designsystem`: `TroskoTheme`, an empty wrapper.
- `:app`: `TroskoApplication`, `AppContainer`, `TroskoNavDisplay` and the `feature/placeholder`
  package, whose screen shows its message only after the real database has answered.
- Android backup is switched on with the limits from
  [decisions/0020](decisions/0020-android-backup.md).

What the architecture describes but nobody has built yet: the `Record` domain model and its
repository, any write path, `Period` and the injected `Clock`, parsing and formatting of
amounts, settings storage (DataStore is in the version catalog but unused), export and restore,
seeding of the built-in categories, the application-wide scope for writes, and migrations.

**Schema version 1 is not frozen.** A release build was on the owner's phone for a few minutes
as a test, signed with the debug key and uninstalled again, so no release installation with
data exists. The schema freezes the first time a release build is installed to be used; write
that here when it happens.

## Completed in the last session

Session 2 produced the architecture plan, got the owner's approval, and implemented it up to
the skeleton. The owner's answers are recorded below and in the decision records.

What was verified by actually running it, on 2026-10-07:

- `check`, `assembleDebug` and `assembleRelease` (unsigned, with R8) pass.
- JVM tests: `MoneyTest` 16 tests, `PlaceholderViewModelTest` 2 tests, no failures.
- `connectedDebugAndroidTest` on the `Medium_Phone_API_36.1` emulator: `TroskoDatabaseTest`
  4 tests, `PlaceholderScreenTest` 1, `RequestedPermissionsTest` 1, no failures. This covers
  the database opening on a device through the bundled SQLite driver, the composite foreign
  key, and the path from the database to the screen.
- KSP 2.3.12 and kotlinx.serialization 1.11.0 work with Kotlin 2.4.20, which neither
  documents.
- The compose-rules checks are live: a deliberately wrong composable failed `spotlessCheck`.
- On the owner's phone (Samsung SM-G998B, Android 15): the same six instrumented tests pass,
  and the debug build was installed and launched, with `MainActivity` resumed and the database
  files created.
- The release build runs on the owner's phone. The unsigned release APK was signed with the
  local debug key, installed, launched and then uninstalled. It showed the placeholder message,
  which only appears after the database answers, so R8 does not break Room, the bundled SQLite
  driver, Navigation 3 or serialization. No crash was logged.
- Cloud backup and restore work on the owner's phone with the debug build: `bmgr backupnow`
  ran an encrypted full backup of `trosko.db` and its `-wal`, `-shm` and `.lck` files, and after
  uninstalling and reinstalling, Android restored them before first launch and the app opened
  the restored database. A backup is refused ("Backup is not allowed") while the app is in the
  stopped state, for example right after `am force-stop`.
- Both native libraries in the release APK are aligned for 16 KB pages, in the zip
  (`zipalign -c -P 16`) and in their ELF load segments. The bundled SQLite library is about
  1.2 to 1.3 MB per ABI; the universal release APK is 5.8 MB, most of it four copies of SQLite.
- CI passed on the push that contains the skeleton.

What was not verified:

- Backup and restore of a database that has rows in it. The database was empty, because the
  app cannot write yet. Repeat the round trip once it can, and compare the data.
- Device-to-device transfer, which needs a second phone.
- A release build signed with the real upload key, because there is no keystore yet.
- Why the emulator's local test transport rejected the package. The real transport on the
  phone works, so this was not pursued.

## Next

1. **Design session.** Fill in [DESIGN.md](DESIGN.md). The end of
   [ARCHITECTURE.md](ARCHITECTURE.md#what-is-left-to-the-design-session) lists what
   architecture assumes and what it needs from that session, including the look of a category,
   which adds columns to the schema.
2. **The first feature**, recording an expense. It brings the rest of the domain model, the
   first write path and the first real screen, and with it the baseline profile. Confirm its
   scope with the owner first.
3. Before any release build goes onto a phone to be used: create the upload keystore
   ([RELEASE.md](RELEASE.md)), finish the schema, and repeat the backup and restore round trip
   with real rows.

## Open questions for the owner

- **The feature lists in [PRODUCT.md](PRODUCT.md)** are a first draft. Confirm the scope of
  each feature before building it.
- **Should the repository be public or private?**

## Answered by the owner

- The name Troško is final. The first release uses the euro and targets Croatia.
- Commits are authored with `marko7.cirimotic@gmail.com`, set in the repository's local git
  config; do not commit with any other address.
- Backup: device transfer always, cloud only when end-to-end encrypted. No encryption of our
  own. Export as CSV and JSON, with restore from JSON in the first release. No crash reporting
  SDK. A category with records is archived, not deleted. The CSV follows the app language,
  with a decimal comma in Croatian ([decisions/0025](decisions/0025-csv-follows-the-app-language.md)).
- The release build is the owner's own "production", which the owner and friends will use
  before the Play release; its data must survive updates. The debug build's data is disposable.
  Friends move to the Play version through a JSON export and restore.
- Dates are stored as ISO text, keys are UUIDs, features are packages, Room 3. Dependency
  injection was left to the session, which chose manual injection.

## Known issues and things to watch

- The build prints a Gradle deprecation notice that comes from the detekt 1.23.8 plugin. It is
  harmless until Gradle 10. See [decisions/0003](decisions/0003-toolchain-versions.md).
- The build prints "Unable to strip" for `libandroidx.graphics.path.so` and `libsqliteJni.so`.
  It is informational: the NDK strip tool is not wired up and the libraries are packaged as is.
- Gradle 9.8.0 and AGP 9.4.1 are slightly ahead of the range Kotlin 2.4.20 documents as
  tested, and KSP does not document Kotlin 2.4.20 either. Everything passes, but suspect this
  first if something odd appears.
- The bundled SQLite driver keeps a `trosko.db.lck` file beside the database, next to the usual
  `-wal` and `-shm` files. All four are backed up and restored together.
- Android restores a backup only when the installed app's signature matches the one that made
  it. A build signed with a different key starts empty.
- Database tests do not run in CI, because CI has no emulator job.
- The `feature/placeholder` package is deleted when the first real feature lands.
