# 0003. Toolchain versions as of October 2026

- **Status**: accepted
- **Date**: 2026-10-07, bootstrap session, under the owner's instruction to use the latest
  stable releases

## Context

The owner asked for the latest stable Android Gradle Plugin, Kotlin, Compose BOM and Gradle
wrapper, targeting the latest stable Android SDK, with compatibility confirmed rather than
assumed. Versions were read from Google's Maven repository, Maven Central, the Gradle versions
service and the Android SDK repository manifest on the date above.

## Decision

| Tool | Version |
|---|---|
| Gradle wrapper | 9.8.0, distribution checksum pinned |
| Android Gradle Plugin | 9.4.1 |
| Kotlin | 2.4.20, through AGP's built-in Kotlin |
| Compose compiler plugin | 2.4.20 (it is versioned with Kotlin) |
| Compose BOM | 2026.09.00 |
| Compile SDK | 37.2 (Android 17, API 37, minor release 2) |
| Target SDK | 37 |
| Build tools | 37.0.0 |
| JDK | 21 to run Gradle; Java 17 bytecode for the app |

The remaining versions are in `gradle/libs.versions.toml`.

AGP 9 has Kotlin support built in, so the `org.jetbrains.kotlin.android` plugin is not applied.
The Kotlin version is raised above AGP's bundled default by having the Compose compiler plugin
at 2.4.20 on the build classpath.

## Compatibility, as verified

- AGP 9.4 requires Gradle 9.6.0 or newer and supports up to API 37, according to its release
  notes. Gradle 9.8.0 satisfies that.
- Kotlin's documentation lists Kotlin Gradle Plugin 2.4.20 as fully tested with Gradle up to
  9.7.0 and AGP up to 9.3.1. Gradle 9.8.0 and AGP 9.4.1 are one step past that documented
  range. The combination was verified empirically: `check`, debug, release and instrumented
  test builds all pass.
- The detekt 1.23.8 Gradle plugin uses Gradle APIs that are deprecated and scheduled for
  removal in Gradle 10. The build prints a deprecation notice because of it. detekt 2.0 was
  still in alpha on this date.
- Compose UI tests bring in an old Espresso transitively that crashes on Android 14 and newer,
  so Espresso 3.7.0 is pinned explicitly.

## Consequences

- If a Kotlin or AGP oddity appears, the first suspect is running slightly ahead of Kotlin's
  tested range. Dropping the wrapper to Gradle 9.7.x is the fallback.
- Do not move to Gradle 10 until detekt has a stable release that supports it.
- Upgrades are done deliberately, one tool at a time, with the versions looked up online and
  the full check run afterwards. Write a new record when the set changes materially.
- Machines and CI need SDK platform `android-37.2` and build tools `37.0.0`. AGP downloads them
  itself where the SDK licences have been accepted.
