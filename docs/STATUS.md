# Status

The handoff between sessions. Read this first; rewrite it last. It describes the present, so
replace what is stale instead of appending. History lives in git.

Last updated: 2026-10-07, at the end of session 4 (the first feature: recording an expense).

## Current state

The app has its first feature. It starts on a home page that lists the latest entries, and a
button there turns the page to the entry screen, where an expense is recorded: an amount on the
app's own keypad, a category, a date stepped a day at a time, an optional note. A saved record
lands on the page as a sticker and is there after a restart.

- [ARCHITECTURE.md](ARCHITECTURE.md) and [DESIGN.md](DESIGN.md) are complete and were corrected
  for what this session built. Decision records run from 0001 to 0032; 0030 to 0032 are new.
- Four modules: `:app`, `:core:domain`, `:core:data`, `:core:designsystem`.
- Toolchain: Gradle 9.8.0, AGP 9.4.1, Kotlin 2.4.20, Compose BOM 2026.09.00
  ([decisions/0003](decisions/0003-toolchain-versions.md)), with KSP 2.3.12, Room 3.0.3,
  bundled SQLite 2.7.1, Navigation 3 1.2.0, Lifecycle 2.11.0, coroutines 1.11.0,
  kotlinx.serialization 1.11.0 and compose-rules 0.6.7 through Spotless. Nothing was added or
  bumped this session.
- Quality gate and CI: `.\gradlew.bat check`.

What exists in code:

- `:core:domain`: `Money`; `parseAmount` and `AmountInput` (the keypad's rules), neither of
  which touches floating point; `Record`, `NewRecord` with its rules (amount above zero, no
  future date, note length); `Category` with a colour and an icon; `BuiltinCategories`, nine
  expense categories with fixed ids; the `CategoryRepository` and `RecordRepository`
  interfaces.
- `:core:data`: the Room database with the `category` and `record` tables (schema version 1,
  exported to `core/data/schemas/`), seeding of the built-in categories when the database is
  created, `RoomCategoryRepository`, `RoomRecordRepository` (one write method, one query for the
  latest records) and the public `Repositories` class.
- `:core:designsystem`: the theme, and the components `StickerSurface`, `TroskoText`,
  `TroskoButton`, `QuietButton`, `QuietIconButton`, `CategoryChip`, `AmountDisplay`,
  `TroskoTextField`, `RecordRow`, `InkIcon` with twelve glyphs, and `LandingSticker`.
- `:app`: `AppContainer` (repositories, the clock, the application-wide write scope),
  `TroskoNavDisplay` with the page-turn transition, `feature/home`, `feature/expense`, and
  `format/` (amounts, day names, category wording and look, and the record row both features
  show).
- Android backup is on, with the limits from [decisions/0020](decisions/0020-android-backup.md).

What the documents describe but nobody has built yet:

- From the product: recording an income, editing and deleting, history with filters, the
  overview of a period and change over time, managing categories, export and restore.
- From the architecture: `Period`, export and restore, migrations, settings storage, built-in
  income sources, the baseline profile and macrobenchmark
  ([decisions/0032](decisions/0032-baseline-profile-in-its-own-session.md)).
- From the design: bars and charts, the empty state with the mascot, the tilt of the page turn,
  the error state of the text field, the pressed state of a record row, and all mascot artwork
  including the launcher icon and splash screen. The delighted mascot beside a saved record
  waits for that artwork.

**Schema version 1 is not frozen.** It changed in place this session (a colour and an icon on
`category`, an index on `record.created_at`). No release build was installed. The schema
freezes the first time a release build is installed to be used; write that here when it
happens.

## Completed in the last session

Session 4 built recording an expense. The owner answered the product questions and chose the
layout from three drawn variants (published as a private page, not stored in the repository);
the choices are in [decisions/0030](decisions/0030-home-page-and-own-keypad.md) and
[0031](decisions/0031-built-in-categories.md). The placeholder feature was removed.

What was verified by actually running it, on 2026-10-07, all on the owner's phone (Samsung
SM-G998B, Android 15, Croatian):

- `check`, `assembleDebug` and `assembleRelease` (unsigned, with R8) pass. The release APK is
  6.95 MB.
- `connectedDebugAndroidTest`: `:app` 18 tests, `:core:data` 13, `:core:designsystem` 2, no
  failures.
- The debug build was installed and used through adb: amounts typed on the keypad, categories
  chosen, the date stepped back, a note typed on Samsung's keyboard, eight expenses saved. They
  were on the home page after a force-stop and relaunch.
- Looking at it found three faults that the tests had not, all fixed: a landed record stayed
  enlarged and off the page (now covered by `LandingStickerTest`), category names broke in the
  middle of a word, and at 200 percent text the button's word and a record's name did too.
- The light theme, by switching the phone to it and back: paper, ink and stickers are right on
  both screens.
- Text at 200 percent, by setting the font scale to 2.0 and back: the entry screen scrolls,
  the chips fall into two columns, nothing is cut off or broken mid-word, and a record's amount
  moves under its name.
- A press, live: a key and the main button were captured while held down; each sits flat in its
  shadow's place.
- Reduced motion, with the three system animation scales at zero and then restored: the amount
  and the landed record sit level, and saving still works. A press still moves the button, at
  once, as the design says.
- Backup with real rows: `bmgr backupnow` succeeded on Google's transport, the debug app was
  uninstalled and installed again, and the home page showed the same eight records, compared
  line by line.
- Formatting on the device matches the JVM for the cases tested in both, apart from which space
  and which minus sign the locale data uses.

What was not verified:

- **TalkBack by ear or by gesture.** TalkBack was switched on and focused the heading, but
  swipes injected through adb did not move its focus, so the reading order and the spoken text
  were not observed. What was checked instead: the accessibility tree read from the phone has
  a label for every key, chip, arrow and row, and the screen tests assert that a chosen chip
  is selected and that the button is disabled until it may be pressed. The owner should walk the entry screen once with TalkBack: the pad is meant
  to be read before the page above it, and "Zapisano." is meant to be announced after a save.
- Whether the landing and the page turn look right in motion. Only still frames were seen; the
  owner has to watch them. The same goes for how often anything should vary.
- The `reduced` fade. With the system's animator scale at zero Compose finishes animations at
  once, so the fade is believed to be an immediate change; that was not measured.
- The page turn following a predictive back gesture, and anything in landscape or on a tablet.
- Typing the amount on a physical keyboard, and the dashed focus outline. Both are built; no
  keyboard was attached. The digits only reach the keypad while something on the screen has
  focus.
- A failed save on a real database. The message and the kept form are tested with a fake that
  refuses the write.
- Process death in the middle of an entry on the device. The view model test restores a form
  from saved state; the phone was not made to kill the process.
- The emulator. Nothing ran on it this session.
- The release build on a device with this feature, device-to-device transfer, and a release
  build signed with the real upload key, as before.

## Next

1. **The owner's look at the result**: the motion of the landing and the page turn, a pass
   with TalkBack, and the names "Vani" and "Stan".
2. **The baseline profile and macrobenchmark**, in a short session of their own
   ([decisions/0032](decisions/0032-baseline-profile-in-its-own-session.md)). The module, the
   plugin and three libraries need the owner's approval.
3. **The next feature.** The owner said the home page will hold statistics, so the overview of
   a period is the natural candidate; editing and deleting a record is the other, because a
   wrong entry cannot be corrected today. Confirm the choice and the scope with the owner.
4. **The mascot's final artwork**, drawn from the approved sketches in `docs/design/mascot/`,
   then the launcher icon and splash screen. Show the owner drawings; do not describe them.
5. Before any release build goes onto a phone to be used: create the upload keystore
   ([RELEASE.md](RELEASE.md)), finish the schema, and repeat the backup and restore round trip
   on that build.

## Open questions for the owner

- **The feature lists in [PRODUCT.md](PRODUCT.md)** are a first draft. Confirm the scope of
  each feature before building it.
- **Should the repository be public or private?**
- **What the home page shows** once statistics exist, and whether the list of latest entries
  stays on it.

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
  sound; the two fonts may be bundled. The mascot sketches in `docs/design/mascot/` are
  approved.
- The first feature: the app starts on a home page, entry is its own screen with the app's own
  keypad, zero cannot be saved, and the nine built-in categories are accepted for now
  ([decisions/0030](decisions/0030-home-page-and-own-keypad.md),
  [0031](decisions/0031-built-in-categories.md)).
- On the owner's phone a session may, when it says so first: clear the debug app's data, switch
  the theme, the font scale and the animation scales and put them back, switch TalkBack on and
  off, and run a backup and restore of the debug app. Ask again each session.

## Known issues and things to watch

- **While schema version 1 is a draft, a changed schema makes an installed debug app crash at
  start** with "Room cannot verify the data integrity": the version number is the same and the
  contents are not, and the destructive fallback does not cover that case. Clear its data:
  `adb shell pm clear dev.cirimo.trosko.debug`. The same crash appears after a reinstall on the
  owner's phone if the cloud backup was made with an older draft, because Android restores it;
  clear the data again, and the next backup replaces the old one. The emulator's debug app
  still holds the schema from before this session.
- `connectedDebugAndroidTest` uninstalls the app when it finishes, and the records in it are
  gone unless a backup brings them back. Run the tests before entering anything worth keeping.
- A wrong entry cannot be edited or deleted yet. On the debug build that is harmless.
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
- Database, screen and component tests do not run in CI, because CI has no emulator job.
- The compose-rules allowlist for composition locals could not be set through `.editorconfig`
  under Spotless; the setting was ignored. `TroskoTheme.kt` suppresses that one rule for itself
  instead, with a comment saying why.
- Spotless rewrites a `when` branch of the form `X -> Unit` into a block, which then fails the
  build as an unused expression. Write such a branch another way.
- detekt only exempts a private preview function from "unused" when it carries `@Preview`
  itself. A multi-preview annotation of our own is not recognised, so a preview for both themes
  carries two `@Preview` annotations.
- A text that must shrink to fit uses `BasicText` with `autoSize` directly; `TroskoText` does
  not offer it. `TroskoButton` and `CategoryChip` show how.
- TalkBack on this phone opens its tutorial the first time it is switched on, and its focus
  cannot be moved by `adb shell input swipe`.
- Shantell Sans is 1.3 MB on disk because it carries Cyrillic and variation axes the app does
  not use. Subsetting it is an easy size win that has not been taken.
- `app/.../format/Wording.kt` holds several small public functions in one file, against the
  one-declaration-per-file convention, because they are one mapping read together.
