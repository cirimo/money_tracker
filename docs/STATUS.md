# Status

The handoff between sessions. Read this first; rewrite it last. It describes the present, so
replace what is stale instead of appending. History lives in git.

Last updated: 2026-10-07, after session 5 (correcting and deleting a record).

## Current state

The app records expenses and lets a wrong one be put right. It starts on a home page that lists
the latest entries; a button there turns the page to the entry screen, where an expense is
recorded on the app's own keypad. Pressing a record on either list opens the correction screen:
the same pad filled with what was written down, the record itself lying above it, a calendar
behind the date, and a delete button that asks once.

- [ARCHITECTURE.md](ARCHITECTURE.md) and [DESIGN.md](DESIGN.md) are complete and were corrected
  for what this session built. Decision records run from 0001 to 0033; 0033 is new.
- Four modules: `:app`, `:core:domain`, `:core:data`, `:core:designsystem`.
- Toolchain: Gradle 9.8.0, AGP 9.4.1, Kotlin 2.4.20, Compose BOM 2026.09.00
  ([decisions/0003](decisions/0003-toolchain-versions.md)), with KSP 2.3.12, Room 3.0.3,
  bundled SQLite 2.7.1, Navigation 3 1.2.0, Lifecycle 2.11.0, coroutines 1.11.0,
  kotlinx.serialization 1.11.0 and compose-rules 0.6.7 through Spotless. Nothing was added or
  bumped this session.
- Quality gate and CI: `.\gradlew.bat check`.

What exists in code:

- `:core:domain`: `Money`; `parseAmount` and `AmountInput` (the keypad's rules, and
  `AmountInput.of` to turn a stored amount back into keys), none of which touches floating
  point; `Record`, `NewRecord` with its rules (amount above zero, no future date, note length);
  `Category` with a colour and an icon; `BuiltinCategories`, nine expense categories with fixed
  ids; `CalendarMonth`, a month laid out in weeks; the `CategoryRepository` and
  `RecordRepository` interfaces, the latter with `add`, `replace`, `delete`, `observe` and
  `observeLatest`.
- `:core:data`: the Room database with the `category` and `record` tables (schema version 1,
  exported to `core/data/schemas/`), seeding of the built-in categories when the database is
  created, `RoomCategoryRepository`, `RoomRecordRepository` and the public `Repositories` class.
- `:core:designsystem`: the theme, and the components `StickerSurface`, `TroskoText`,
  `TroskoButton`, `QuietButton`, `QuietIconButton`, `CategoryChip`, `AmountDisplay`,
  `TroskoTextField`, `RecordRow` (which can be pressed), `DayCell`, `InkIcon` with thirteen
  glyphs, and `LandingSticker`.
- `:app`: `AppContainer` (repositories, the clock, the application-wide write scope),
  `TroskoNavDisplay` with the page-turn transition and the result bus, `feature/home`,
  `feature/expense` (the entry screen, the correction screen, and the pad, keypad, date stepper
  and calendar they are built from), and `format/` (amounts, day names, category wording and
  look, the record row and heading both lists show, and `RecordChange`, what the correction
  screen tells a list).
- Android backup is on, with the limits from [decisions/0020](decisions/0020-android-backup.md).

What the documents describe but nobody has built yet:

- From the product: recording an income (and so correcting one), history with filters, the
  overview of a period and change over time, managing categories, export and restore.
- From the architecture: `Period` (only `CalendarMonth` exists in its package), export and restore, migrations, settings storage, built-in
  income sources, the baseline profile and macrobenchmark
  ([decisions/0032](decisions/0032-baseline-profile-in-its-own-session.md)).
- From the design: bars and charts, the empty state with the mascot, the tilt of the page turn,
  the error state of the text field, and all mascot artwork
  including the launcher icon and splash screen. The delighted mascot beside a saved record
  waits for that artwork.

**Schema version 1 is not frozen.** It did not change this session. No release build was installed. The schema
freezes the first time a release build is installed to be used; write that here when it
happens.

## Completed in the last session

Session 5 built correcting and deleting a record, with a calendar for the date. The owner chose
from drawn variants (published as a private page, not stored in the repository) and agreed with
every recommendation; the choices are in
[decisions/0033](decisions/0033-correcting-and-deleting-a-record.md).

What was verified by actually running it, on 2026-10-07, on the owner's phone (Samsung SM-G998B,
Android 15, Croatian, dark theme, text at 100 percent):

- `check`, `assembleDebug` and `assembleRelease` (unsigned, with R8) pass on the final tree. The
  release APK is 6.98 MB.
- `connectedDebugAndroidTest` on the final tree: `:app` 28 tests, `:core:data` 19,
  `:core:designsystem` 4, no failures.
- Before the calendar was added, the debug build was installed and used through adb. A record
  was opened from the home page, its amount corrected twice (12,50 to 115,20 by a slip of the
  script, then to 15,20), and saved. The page turned back, the heading said "Ispravljeno." and
  the record sat tilted in its old place. After a force-stop and relaunch the home page showed
  15,20.
- Another record was opened, "Obriši" pressed, the question shown in place of the heading, and
  the delete confirmed. The home page said "Obrisano." and listed seven records; the same seven
  were read from the accessibility tree after a force-stop and relaunch.
- Looking at it found one fault the tests had not: the correction screen reserved the entry
  screen's tall page above the pad and so had to be scrolled to reach the bottom keys. The page
  now takes only the height it needs, and the whole screen fits on this phone (360 by 800 dp).
- After the tests uninstalled the app, a reinstall brought the earlier records back from
  Android's backup, as noted under known issues.

What was not verified:

- **The calendar on a device, by eye.** The phone dropped off adb just after the final build was
  installed. The calendar is covered by the screen tests (a day is chosen and written, no day
  after today is offered, the month does not turn past the current one) and those ran on the
  phone, but nobody has looked at it: its spacing, the day cells at 360 dp wide, the underline
  on today, or how it reads with enlarged text.
- **The light theme, text at 200 percent and reduced motion for everything built this session.**
  None of the three was switched on.
- **A record opened from the entry screen's list.** Only the home page's list was pressed on the
  device. Both lists use the same row and the same navigation call.
- **The second landing and the rows moving up after a delete, in motion.** Only still frames
  taken two seconds later were seen.
- **TalkBack.** Not switched on this session. The screen tests assert that a pressable row is
  one button, that the chosen day and chip say they are selected, and that days after today are
  not offered; the spoken order and wording were not heard. The owner's pass from session 4 is
  still owed too.
- A failed correction or delete on a real database; both messages are tested with a fake that
  refuses the write. A record that vanishes while its correction screen is open is tested the
  same way.
- Process death on the correction screen on the device. The view model test restores the
  changed form from saved state.
- The page turn following a predictive back gesture, landscape, tablets, a physical keyboard
  and its focus outlines, as before.
- The emulator. Nothing ran on it this session.
- The release build on a device, device-to-device transfer, and a release build signed with the
  real upload key, as before.

## Next

1. **The owner's look at the result**: from session 4, the motion of the landing and the page
   turn, a pass with TalkBack, and the names "Vani" and "Stan"; from session 5, the correction
   screen and its calendar in both themes and at 200 percent, the second landing, and the
   English words "Correction" and "Keep". Everything listed above as not verified can be checked
   in the same sitting.
2. **The baseline profile and macrobenchmark**, in a short session of their own
   ([decisions/0032](decisions/0032-baseline-profile-in-its-own-session.md)). The module, the
   plugin and three libraries need the owner's approval.
3. **Recording an income**, then **the statistics on the home page**. Savings and the balance
   mean nothing until income can be recorded, so income comes first. The proposal for the
   statistics, made in words and not yet seen drawn or approved:
   - this month so far: spent, earned, and the difference between them, as three figures;
   - spending by category for the month, as the horizontal bars [DESIGN.md](DESIGN.md#charts)
     describes;
   - month over month for the last six months, as its pairs of bars;
   - what was saved over the last three full months, in total and month by month;
   - this month up to today's day beside last month up to the same day, stated as two
     figures and never as a verdict.
   An average per day would need division, which `Money` does not have on purpose
   ([ARCHITECTURE.md](ARCHITECTURE.md#money)); decide the rounding before proposing one.
   Draw the alternatives for the owner before building any of it.
4. **The mascot's final artwork**, drawn from the approved sketches in `docs/design/mascot/`,
   then the launcher icon and splash screen. Show the owner drawings; do not describe them.
5. Before any release build goes onto a phone to be used: create the upload keystore
   ([RELEASE.md](RELEASE.md)), finish the schema, and repeat the backup and restore round trip
   on that build.

## Open questions for the owner

- **Which statistics the home page shows.** A proposal is under Next; the owner decides after
  seeing it drawn. Whether the list of latest entries stays on the home page is part of that.

## Answered by the owner

- The name Troško is final. The first release uses the euro and targets Croatia.
- The feature lists in [PRODUCT.md](PRODUCT.md) are final for the first version.
- The repository is public for now, while the work is being done. Nothing private may be
  committed, and that includes real bank statements: the owner has offered some as samples
  for the later import feature, and they must stay outside the repository.
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
- Correcting an amount means pressing backspace once for every key of the old one (five times
  for 12,50); there is no key that clears it. Whether that is too slow is for the owner to say.
- The entry screen has no calendar, only the arrows; the calendar is on the correction screen.
  Giving entry the same one is a small change that was not asked for.
- A pressable record row is announced by TalkBack with the system's default "double tap to
  activate". A custom action word ("ispravi") was left out because its Croatian grammar inside
  TalkBack's sentence could not be checked.
- On a phone 360 dp wide, seven day cells of the minimum touch size need 8 dp more than the
  content width, so the calendar reaches 4 dp into each gutter.
- detekt allows a class ten functions. Both view models of the expense feature are at the limit;
  the next thing either must do needs a function merged or a piece moved out.
- In Git Bash, adb paths on the phone such as `/sdcard/x` are rewritten into Windows paths
  unless `MSYS_NO_PATHCONV=1` is set.
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
