# Status

The handoff between sessions. Read this first; rewrite it last. It describes the present, so
replace what is stale instead of appending. History lives in git.

Last updated: 2026-10-07, at the end of session 3 (design).

## Current state

Architecture and design are both decided and written down, and both exist in code as a
skeleton. There are still no features: the app shows one placeholder screen, now drawn with the
real theme.

- [ARCHITECTURE.md](ARCHITECTURE.md) is complete, with decision records 0012 to 0025.
- [DESIGN.md](DESIGN.md) is complete, with decision records 0026 to 0029.
- Four modules: `:app`, `:core:domain`, `:core:data`, `:core:designsystem`.
- Toolchain: Gradle 9.8.0, AGP 9.4.1, Kotlin 2.4.20, Compose BOM 2026.09.00
  ([decisions/0003](decisions/0003-toolchain-versions.md)), with KSP 2.3.12, Room 3.0.3,
  bundled SQLite 2.7.1, Navigation 3 1.2.0, Lifecycle 2.11.0, coroutines 1.11.0,
  kotlinx.serialization 1.11.0 and compose-rules 0.6.7 through Spotless.
- Quality gate and CI: `.\gradlew.bat check`.

What exists in code:

- `:core:domain`: `Money` with tests, `RecordKind`, `Category`, `CategoryId`, `CategoryName`
  and the `CategoryRepository` interface with one read method.
- `:core:data`: the Room database with the `category` and `record` tables (schema version 1,
  exported to `core/data/schemas/`), their DAOs, `RoomCategoryRepository` and the public
  `Repositories` class.
- `:core:designsystem`: `TroskoTheme` with colours for both themes, the type scale on the two
  bundled fonts, shapes, sizes and motion specs; and the components `StickerSurface`,
  `TroskoText` and `TroskoButton`.
- `:app`: `TroskoApplication`, `AppContainer`, `TroskoNavDisplay` and the `feature/placeholder`
  package, whose screen shows its message on a yellow sticker once the database has answered.
- Android backup is on, with the limits from [decisions/0020](decisions/0020-android-backup.md).

What the documents describe but nobody has built yet:

- From the architecture: the `Record` domain model and its repository, any write path, `Period`
  and the injected `Clock`, parsing and formatting of amounts, export and restore, seeding of
  the built-in categories, the application-wide scope for writes, migrations, and the two
  columns for a category's colour and icon.
- From the design: every component in [DESIGN.md](DESIGN.md#components) marked "not built", the
  icon set, charts, the page-turn transition, the landing-sticker animation, the keyboard focus
  outline on `TroskoButton`, and all mascot artwork including the launcher icon and splash
  screen.

**Schema version 1 is not frozen.** A release build was on the owner's phone for a few minutes
as a test, signed with the debug key and uninstalled again, so no release installation with
data exists. The schema freezes the first time a release build is installed to be used; write
that here when it happens.

## Completed in the last session

Session 3 was the design session. The owner chose the direction from drawn alternatives (a
hand-drawn notebook, then the "fat marker and stickers" rendering of it), approved a full design
sheet, and agreed to bundling the fonts. The theme and first components were then built.

The drawn alternatives and the design sheet were published as private pages for the owner, not
stored in the repository. [DESIGN.md](DESIGN.md) is the record of what was decided.

What was verified by actually running it, on 2026-10-07:

- `check`, `assembleDebug` and `assembleRelease` (unsigned, with R8) pass.
- `connectedDebugAndroidTest` on the `Medium_Phone_API_36.1` emulator: `TroskoDatabaseTest`
  4 tests, `PlaceholderScreenTest` 1, `RequestedPermissionsTest` 1, no failures.
- The debug build was installed on the emulator and looked at in both themes: paper and ink
  swap correctly, the sticker has its outline and hard shadow, and Shantell Sans renders,
  including "š".
- On the owner's phone (Samsung SM-G998B, Android 15, dark theme, Croatian): the same six
  instrumented tests pass, and the themed placeholder was looked at; the Croatian text renders
  in Shantell Sans with its diacritics.
- Both fonts were checked at their source for licence (OFL 1.1) and Latin Extended coverage.
- The fonts add about 0.8 MB to the release APK: 5.79 MB before, 6.57 MB after.
- Text contrast was computed for both themes; the figures are in [DESIGN.md](DESIGN.md#colour).

Still true from session 2, and not re-run this session: the release build runs on the owner's
phone when signed with the debug key; an encrypted cloud backup and a restore on reinstall work
on the owner's phone with an empty database; both native libraries are aligned for 16 KB pages.

What was not verified:

- The light theme on the owner's phone, which is set to dark.
- `TroskoButton`: it compiles and has a preview, but no screen uses it yet, so its press
  animation and its behaviour with TalkBack and under reduced motion have not been seen running.
- Reduced motion as a whole. The theme reads the system setting, but nothing was run with
  animations switched off.
- Font scaling at 200 percent.
- The release build with the fonts on a device (only built, not run).
- Backup and restore of a database with rows in it, device-to-device transfer, and a release
  build signed with the real upload key, as before.

## Next

1. **The first feature: recording an expense.** It brings the rest of the domain model, the
   first write path, the entry screen with its components (amount display, category chip, text
   field, record row), the landing-sticker animation and the baseline profile. Confirm its
   scope with the owner first, and decide then whether entry is a screen or a sheet.
2. **The mascot's final artwork**, then the launcher icon and splash screen. Show the owner
   drawings; do not describe them.
3. Before any release build goes onto a phone to be used: create the upload keystore
   ([RELEASE.md](RELEASE.md)), finish the schema, and repeat the backup and restore round trip
   with real rows.

## Open questions for the owner

- **The feature lists in [PRODUCT.md](PRODUCT.md)** are a first draft. Confirm the scope of
  each feature before building it.
- **Should the repository be public or private?**
- **Is the second mascot sketch right?** The owner asked for more soul and authenticity than the
  first one had. A second sketch was drawn and shown (a worn, lopsided coin with one raised
  eyebrow and thin limbs) and had not been answered when the session ended.

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
- Design: the notebook with a fat marker and stickers; a discreet mascot; "ti" and a short,
  relaxed voice in Croatian; light and dark themes following the system; no haptics and no
  sound; the two fonts may be bundled.

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
- A backup is refused while the app is in the stopped state, for example right after
  `am force-stop`.
- Database tests do not run in CI, because CI has no emulator job.
- The compose-rules allowlist for composition locals could not be set through `.editorconfig`
  under Spotless; the setting was ignored. `TroskoTheme.kt` suppresses that one rule for itself
  instead, with a comment saying why.
- Spotless rewrites a `when` branch of the form `X -> Unit` into a block, which then fails the
  build as an unused expression. Write such a branch another way.
- Shantell Sans is 1.3 MB on disk because it carries Cyrillic and variation axes the app does
  not use. Subsetting it is an easy size win that has not been taken.
- The `feature/placeholder` package is deleted when the first real feature lands.
