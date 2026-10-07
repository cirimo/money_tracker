# Status

The handoff between sessions. Read this first; rewrite it last. It describes the present, so
replace what is stale instead of appending. History lives in git.

Last updated: 2026-10-07, at the end of session 1 (bootstrap).

## Current state

The project skeleton exists and is verified. There is one module, `:app`, with a single
unstyled placeholder screen. No architecture and no design have been chosen yet, on purpose.

- Application id `dev.cirimo.trosko`, app name Troško, min SDK 26, target SDK 37.
- Toolchain: Gradle 9.8.0, AGP 9.4.1, Kotlin 2.4.20, Compose BOM 2026.09.00. Details and
  caveats are in [decisions/0003](decisions/0003-toolchain-versions.md).
- Quality gate: `.\gradlew.bat check` (Spotless with ktlint, detekt, Android Lint with warnings
  as errors, unit tests, compilation of instrumented tests).
- CI: a GitHub Actions workflow runs the same check on every push and pull request.
- The instruction system (CLAUDE.md, `docs/`, `.claude/`) is written.

## Completed in the last session

Session 1 bootstrapped everything from an empty repository:

- Gradle wrapper, version catalog, `build-logic` convention plugins, the `:app` module with
  debug and release build types, R8 shrinking and release signing from an untracked file.
- English and Croatian string resources, one unit test, one Compose UI test.
- Formatting, static analysis, Lint, `.editorconfig`, `.gitignore`, `.gitattributes`, CI.
- All documentation, eleven decision records, path-scoped rules and the permission allowlist.

What was verified by actually running it, on 2026-10-07:

- `check` passes. `assembleDebug` and `assembleRelease` (unsigned) build.
- `connectedDebugAndroidTest` passes on the `Medium_Phone_API_36.1` emulator: 1 test, 0 failures.
- The debug build was installed on the owner's phone (Samsung SM-G998B, Android 15) with
  `installDebug` and launched; `MainActivity` was confirmed as the resumed activity.

What was not verified:

- CI has never run, because nothing has been pushed yet. The workflow is untested. In
  particular it relies on AGP downloading SDK platform 37.2 and build tools 37.0.0 on the
  runner. Watch the first run after the first push and fix it if it fails.
- The release build has never been run on a device, because there is no keystore yet.
- The instrumented test was run on the emulator only, not on the physical phone.
- The permission allowlist in `.claude/settings.json` was written from the documentation and
  has not been observed in use. If a later session is still prompted for routine Gradle or adb
  commands, adjust the patterns there.

## Next

1. **Architecture session.** Fill in [ARCHITECTURE.md](ARCHITECTURE.md), which lists the
   decisions to make. Do not start features before this.
2. **Design session.** Fill in [DESIGN.md](DESIGN.md), which lists what to define.
3. Only then, the first feature. [PRODUCT.md](PRODUCT.md) says recording an expense is the
   most important path.

## Open questions for the owner

- **The feature lists in [PRODUCT.md](PRODUCT.md)** are a first draft. The owner will refine
  them and add features later, so confirm the scope of each feature before building it.
- **Should the repository be public or private?** This affects how careful to be with what is
  written in documents and issues, though never with secrets, which stay out either way.

## Answered by the owner in session 1

- The name Troško is final.
- The first release uses the euro and targets Croatia.
- Commits to this repository are authored with `marko7.cirimotic@gmail.com`. It is set in the
  repository's local git config; do not commit with any other address.

## Known issues and things to watch

- The build prints a Gradle deprecation notice that comes from the detekt 1.23.8 plugin. It is
  harmless until Gradle 10. See [decisions/0003](decisions/0003-toolchain-versions.md).
- The build prints "Unable to strip libandroidx.graphics.path.so". It is informational: the NDK
  strip tool is not wired up and the library is packaged as is.
- Gradle 9.8.0 and AGP 9.4.1 are slightly ahead of the range Kotlin 2.4.20 documents as
  tested. Everything passes, but keep it in mind if something odd appears.
- `ToolchainSmokeTest` is a placeholder and should be deleted when the first real unit test
  lands.
