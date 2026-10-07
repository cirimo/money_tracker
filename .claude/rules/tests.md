---
paths:
  - "**/src/test/**"
  - "**/src/androidTest/**"
---

# Tests

What to test and why is in [docs/CONVENTIONS.md](../../docs/CONVENTIONS.md#testing). This file
covers how tests are written and run in this project.

## Two kinds

- `src/test/kotlin` holds JVM unit tests (JUnit 4). They run in `check` and in CI. Put a test
  here whenever the code under test does not need Android.
- `src/androidTest/kotlin` holds instrumented tests that need a device or emulator. `check`
  compiles them but does not run them. Run them with `.\gradlew.bat connectedDebugAndroidTest`.

## Writing them

- Name the test class after what it tests, with a `Test` suffix, in the same package.
- Unit test names are sentences in backticks: `` `adding two amounts keeps the currency` ``.
  Instrumented test names are camelCase, because spaces in method names are not valid in DEX
  below API 30 and the minimum SDK is 26.
- Arrange, act, assert, with a blank line between the three. One behaviour per test.
- Never assert against a hardcoded user-facing string. Read it from resources, as
  `AppLaunchTest` does, so the test passes whatever language the device is set to. The
  owner's phone is set to Croatian.
- For Compose, use the `v2` test rules (`androidx.compose.ui.test.junit4.v2`). The older ones
  are deprecated, and deprecation warnings fail the build.
- Find nodes by text from resources or by semantics. Add a test tag only when nothing
  user-visible identifies the node.
- Anything involving money is tested for zero, negative values, large values and rounding
  boundaries, and formatting is tested in both locales with the locale set explicitly in the
  test.
- Tests must not depend on the current date, time zone, device language, network or each
  other's leftovers. Pass such things in.

## Running on a device

- With several devices attached, choose one with the `ANDROID_SERIAL` environment variable.
  `adb devices` lists them; `adb` must be called by its full path, see [CLAUDE.md](../../CLAUDE.md).
- A physical phone must be unlocked with the screen on, or Compose tests fail for reasons that
  have nothing to do with the code.
- An emulator named `Medium_Phone_API_36.1` exists on the owner's machine. Start it headless
  with `emulator.exe -avd Medium_Phone_API_36.1 -no-window -no-audio` from the SDK's
  `emulator` folder and stop it with `adb -s emulator-5554 emu kill` when you are done.
- Results are written to `<module>/build/outputs/androidTest-results/` and an HTML report to
  `<module>/build/reports/androidTests/`, for `app` and for `core/data`.
- Database tests live in `core/data/src/androidTest` and open an in-memory database with the
  bundled SQLite driver, as `TroskoDatabaseTest` does. They cannot run on the JVM.
- A view model test replaces the main dispatcher and collects the state in `backgroundScope`,
  as `ExpenseEntryViewModelTest` does; without a collector `stateIn` never starts.
- Fakes of the repository interfaces, and a clock a test can set, are in
  `app/src/sharedTest/kotlin`. That folder is compiled into both the JVM tests and the
  instrumented tests of `:app`, so there is one copy of each fake.
- `connectedDebugAndroidTest` uninstalls the app when it finishes, and its data with it. On a
  phone with cloud backup, the next install then restores the last backup; see the notes in
  [docs/STATUS.md](../../docs/STATUS.md).
- Say in your report whether you ran the instrumented tests, and on what.
