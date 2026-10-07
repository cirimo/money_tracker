# Release readiness

The checklist for getting Troško onto Google Play. The goal from day one is a public release, so
this list exists to make sure nothing is done now that would block that later.

Status values: **done**, **not started**, **not applicable yet** (it cannot be done until
something else exists; the note says what).

Google Play's requirements change. Before acting on any item, check the current policy in the
Play Console help rather than trusting this file, and correct the file if it is out of date.
Last reviewed: 2026-10-07.

## Signing and key management

No secret may ever be committed: no keystore, no password, no service account key. This holds
even for a "temporary" or "debug" one, because git history is permanent and the repository is
hosted on GitHub. See [decision 0006](decisions/0006-no-secrets-in-git.md).

| Item | Status | Notes |
|---|---|---|
| Release signing reads from an untracked file | done | `keystore.properties` in the repo root, template in `keystore.properties.example`. When absent, `assembleRelease` produces an unsigned APK and everything else still works. |
| Secrets and keystores are git-ignored | done | See `.gitignore`. |
| Create the upload keystore | not started | Generate with `keytool`, store it outside the repository. Needed before the first release build is handed to anyone, the owner's friends included. |
| Plan the move from sideloaded builds to Play | not started | Release builds given to friends before the Play release are signed with the upload key, while Play by default signs with a key Google holds, so the Play version cannot update them in place. The owner decided those users move across with a JSON export and restore ([decision 0021](decisions/0021-export-and-restore.md)). Check the current Play App Signing options before the first upload. |
| Back up the upload keystore and its passwords | not started | At least two separate places, for example a password manager and an offline copy. |
| Enrol in Play App Signing | not applicable yet | Done in the Play Console when the app is created. Google then holds the app signing key and the local key is only the upload key, which can be reset if lost. |
| Decide how CI signs release builds | not started | If CI ever builds releases, the keystore comes from encrypted repository secrets, never from the repository. |

## Identity and versioning

| Item | Status | Notes |
|---|---|---|
| Application id | done | `dev.cirimo.trosko`, permanent once published. [Decision 0008](decisions/0008-application-id.md). |
| App name | done | Troško. [Decision 0007](decisions/0007-app-name.md). Check for trademark conflicts and name availability on Play before launch. |
| Versioning scheme | not started | `versionCode` is 1 and `versionName` is `0.1.0`, set by hand in `app/build.gradle.kts`. Decide the scheme (semantic versioning, how `versionCode` increases) before the first upload. `versionCode` must increase with every upload and can never be reused. |
| Release notes process | not started | |

## Target API and technical requirements

| Item | Status | Notes |
|---|---|---|
| Target SDK meets Play's requirement | done | Target SDK 37, the newest stable. Play requires new apps and updates to target a recent API level and raises the bar every year, so this needs revisiting annually. |
| Minimum SDK chosen | done | 26. [Decision 0009](decisions/0009-min-sdk-26.md). |
| Release build is shrunk and optimised | done | R8 with resource shrinking. Verified to build; not yet verified to run, because it is unsigned. |
| Verify the release build runs on a device | not started | Done once on 2026-10-07 with the release APK signed by the debug key: it ran on the owner's phone and R8 broke nothing. It has not been done with the upload key, which does not exist yet. R8 can break code that debug builds never exercise, so the release build must be tested before every upload. |
| Publish as an Android App Bundle | not started | Play requires `.aab` for new apps: `.\gradlew.bat bundleRelease`. |
| 64-bit and 16 KB page size support | done | We have no native code of our own, but Compose ships a native library (`libandroidx.graphics.path.so`) and the bundled SQLite driver ships another (`libsqliteJni.so`). Both were checked on 2026-10-07 in the release APK with `zipalign -c -P 16` and by reading their ELF load segments. Check again whenever a dependency with native libraries is added or upgraded, and on the bundle before the first upload. |
| Edge-to-edge and predictive back | not applicable yet | The placeholder enables edge-to-edge. Real screens and navigation come with the architecture and design sessions. |
| Permissions kept to the minimum | done | The app requests none, and `RequestedPermissionsTest` fails if a dependency adds `INTERNET` ([decision 0023](decisions/0023-no-network-and-no-crash-sdk.md)). Every permission added later needs a justification here and in the store listing. |
| Baseline profile for startup and animation performance | not applicable yet | Added with the first real screen; see [ARCHITECTURE.md](ARCHITECTURE.md#performance). |

## Privacy and policy

| Item | Status | Notes |
|---|---|---|
| Privacy policy | not started | Required by Play, and must be hosted at a public URL and linked in the listing and the app. Needed even though the app collects nothing, and especially because it handles financial information. |
| Data safety form | not applicable yet | Filled in the Play Console. The facts to answer from: everything is stored on the device, the app has no network access, no analytics and no crash SDK, and the database may be included in Android's own backup as described in the next row. Every SDK added later changes the answers. |
| Backup behaviour decided | done | Device-to-device transfer always; cloud backup only when end-to-end encrypted. [Decision 0020](decisions/0020-android-backup.md). The privacy policy must say this. |
| Backup and restore verified on a phone | not started | Partly done on 2026-10-07: an encrypted cloud backup and a restore on reinstall worked on the owner's phone with the debug build and an empty database. Still to do before release: the same with real rows on a release build, and a device-to-device transfer. |
| Financial features declaration | not applicable yet | Play asks apps to declare financial features. A personal tracker that moves no money and offers no financial services should fall outside the regulated categories; confirm when filling the form. |
| Content rating questionnaire | not applicable yet | Play Console. |
| Target audience and content declaration | not applicable yet | Play Console. |
| Account and data deletion | not applicable yet | Required only if the app creates accounts. The first release has none. |
| Third-party licence notices | not started | The app ships kotlinx.coroutines, kotlinx.serialization and SQLite besides AndroidX, and two fonts under the SIL Open Font License (texts in `core/designsystem/licenses`). It needs a screen or page that shows these notices. |
| Accessibility | not started | Baseline in [CONVENTIONS.md](CONVENTIONS.md#accessibility-baseline). Test with TalkBack and large fonts before release. |

## Store listing

The visual identity is defined in [DESIGN.md](DESIGN.md). The launcher icon, and so the store
icon, waits for the mascot's final artwork; the screenshots wait for real screens.

| Item | Status | Notes |
|---|---|---|
| App icon, 512 by 512 | not applicable yet | The current launcher icon is a placeholder. |
| Feature graphic, 1024 by 500 | not applicable yet | |
| Phone screenshots | not applicable yet | |
| Short description, up to 80 characters | not applicable yet | In English and Croatian. |
| Full description, up to 4000 characters | not applicable yet | In English and Croatian. |
| Category and contact details | not started | Finance category; a public contact email is required. |
| Countries and pricing | not started | |

## Developer account and testing tracks

| Item | Status | Notes |
|---|---|---|
| Google Play developer account | not started | One-time fee and identity verification. Start early, because verification can take time. |
| Internal testing track | not applicable yet | First upload goes here. Up to 100 testers, available within minutes. |
| Closed testing | not applicable yet | New personal developer accounts must run a closed test with a minimum number of testers for a minimum number of days before they may apply for production access. Check the current numbers in the Play Console and plan for this, because it sets the earliest possible release date. |
| Open testing | not applicable yet | Optional. |
| Pre-launch report reviewed | not applicable yet | Play runs the app on real devices automatically and reports crashes and accessibility issues. |
| Production release with staged rollout | not applicable yet | |
| Crash and ANR monitoring after launch | not started | Android vitals in the Play Console is available without adding an SDK. |
