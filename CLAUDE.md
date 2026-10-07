# Troško

Troško is a native Android app for tracking personal finances: what the user spends on, where
income comes from, and how that changes over time. What sets it apart is personality. It is meant
to be fun, with motion, playful interaction and a distinctive look, and deliberately unlike a
sterile banking app. It is in early development and tested on the owner's own phone, but it is
built for a public Google Play release, so nothing is done in a throwaway way.

You start every session with no memory of earlier ones. These files are that memory. Keep them
true, because the next session will believe whatever they say.

## Start and end of every session

Start:

1. Read [docs/STATUS.md](docs/STATUS.md). It says where the project is and what comes next.
2. Read the documents the task touches, using the map below.
3. Run `git status` and `git log --oneline -10` so you know the real state of the tree.

End:

1. Run the full check (below) and fix what it finds.
2. Commit the work in small conventional commits.
3. Rewrite [docs/STATUS.md](docs/STATUS.md) so it describes the project as it is now.
4. Ask before pushing. See the git rules in [docs/WORKFLOW.md](docs/WORKFLOW.md).

## Language

Talk to the owner in Croatian. Write code, comments, commit messages and documentation in English.

## Commands

The machine is Windows 11. In the PowerShell tool use `.\gradlew.bat`; in the Bash tool (Git Bash)
use `./gradlew`. The task names are the same.

| Purpose | Command |
|---|---|
| Run every local check | `.\gradlew.bat check` |
| Fix formatting automatically | `.\gradlew.bat spotlessApply` |
| Build the debug APK | `.\gradlew.bat assembleDebug` |
| Build the release APK (R8, unsigned without a keystore) | `.\gradlew.bat assembleRelease` |
| Install the debug build on the connected phone | `.\gradlew.bat installDebug` |
| Run instrumented tests on the connected device | `.\gradlew.bat connectedDebugAndroidTest` |

`check` is the single gate and CI runs the same thing. It covers Spotless with ktlint (formatting),
detekt (static analysis), Android Lint with warnings as errors, the JVM unit tests, and compiling
the instrumented tests. It does not run instrumented tests, because those need a device. That
includes every database test, so run `connectedDebugAndroidTest` when `core/data` changes.

`adb` is not on PATH. Call it by its full path:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -n dev.cirimo.trosko.debug/dev.cirimo.trosko.MainActivity
```

The debug build has the application id `dev.cirimo.trosko.debug`, so it can sit beside a release
build. With more than one device attached, pick one with `adb -s <serial>` or the `ANDROID_SERIAL`
environment variable.

On a fresh clone, create `local.properties` in the repo root with the SDK path. It is untracked.
Colons must be escaped or Lint fails the build:

```properties
sdk.dir=C\:/Users/<user>/AppData/Local/Android/Sdk
```

## Rules that are not up for negotiation

Each of these is explained, with its reasons, in the linked document. They are listed here because
breaking one is expensive to undo.

- Money is never stored or computed as `Float` or `Double`, not even temporarily.
  See [docs/CONVENTIONS.md](docs/CONVENTIONS.md#money).
- No user-facing text is hardcoded. Every string goes through string resources, in English
  (default) and Croatian. See [docs/CONVENTIONS.md](docs/CONVENTIONS.md#strings).
- No secrets, keystores or signing credentials go into git, ever.
  See [docs/RELEASE.md](docs/RELEASE.md).
- The application id `dev.cirimo.trosko` is permanent. Do not change it.
- Never push without asking the owner first and getting a yes. Never force-push. Never rewrite
  history that has been pushed.
- Do not add a dependency, or bump the toolchain, without asking. Versions are looked up online,
  never recalled from memory. See [.claude/rules/gradle.md](.claude/rules/gradle.md).
- Do not claim something works unless you ran it and saw it pass. Say plainly what you did not run.
- Do not make a failing check pass by disabling or weakening it. After three distinct failed
  attempts at a fix, stop and show the owner the error and what you tried.
- Follow [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): what goes in which module, and what may
  depend on what. Changing it is a decision for the owner, not a side effect of a feature.
- Once a release build has been installed on a real phone, the database schema and the export
  format only change through tested migrations.
  See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md#migrations).
- Design belongs to [docs/DESIGN.md](docs/DESIGN.md), which is still a stub. Do not make its
  decisions as a side effect of other work. Ask instead.

## Map of the documentation

| Document | Read it when |
|---|---|
| [docs/STATUS.md](docs/STATUS.md) | Always, first. Update it last. |
| [docs/WORKFLOW.md](docs/WORKFLOW.md) | Before your first commit of a session, and whenever you are unsure how to proceed, what done means, or what you may do without asking. |
| [docs/CONVENTIONS.md](docs/CONVENTIONS.md) | Before writing or reviewing Kotlin, Compose, tests or resources. |
| [docs/PRODUCT.md](docs/PRODUCT.md) | Before building or changing a feature, and to settle any "is this fun enough or too much" argument. |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Before writing any feature code, and before adding a module, a library, a screen, or anything that stores or moves data. |
| [docs/DESIGN.md](docs/DESIGN.md) | Before touching anything the user sees, hears or feels. |
| [docs/RELEASE.md](docs/RELEASE.md) | Before touching signing, versioning, permissions, the manifest, or anything Google Play cares about. |
| [docs/decisions/](docs/decisions/README.md) | To learn why something is the way it is, and before proposing to change it. |

Rules in `.claude/rules/` load by themselves when you open matching files: Gradle and the version
catalog, Android resources, tests, and these documents. You do not need to read them up front.

## Where things are

```
app/                    the application: composition root, navigation, one package per feature
core/domain/            plain Kotlin: Money, the model, rules, repository interfaces
core/data/              the Room database, storage, export; schemas/ holds exported schemas
core/designsystem/      theme and components, empty until the design session
build-logic/convention/ convention plugins; all shared build configuration lives here
gradle/libs.versions.toml  every version and dependency coordinate
config/                 detekt overrides and the Compose stability list
.github/workflows/      CI
docs/                   everything described in the map above
```

## Keeping these files useful

Keep this file under about 150 lines. It holds only what every session needs; detail belongs in
the documents above. State each fact in one place and link to it from elsewhere, because two
copies drift apart and the next session cannot tell which one is right. When a decision changes,
update the document and add a decision record rather than leaving the old text to mislead.
