# Decision records

Short records of decisions that a future session might otherwise question or undo. Read the
relevant one before proposing to change something; it tells you what was known at the time and
what the choice rules out.

## How to write one

Copy the shape of an existing record. Number it with the next free four-digit number and name
the file `NNNN-short-title.md`. Keep it to what a newcomer needs:

- **Status**: accepted, or superseded by another record.
- **Date** and who decided (the owner, or a session with the owner's agreement).
- **Context**: the situation and the forces at play.
- **Decision**: what was chosen, stated plainly.
- **Consequences**: what this makes easy, what it makes hard, and what it rules out.

An accepted record is not edited afterwards, apart from changing its status. If the decision
changes, write a new record that supersedes it and link the two. Add every new record to the
index below.

## Index

| No. | Decision | Status |
|---|---|---|
| [0001](0001-kotlin-and-jetpack-compose.md) | Native Android with Kotlin and Jetpack Compose | accepted |
| [0002](0002-gradle-build-structure.md) | Gradle Kotlin DSL, version catalog and convention plugins | accepted |
| [0003](0003-toolchain-versions.md) | Toolchain versions as of October 2026 | accepted |
| [0004](0004-money-is-never-floating-point.md) | Money is never floating point | accepted |
| [0005](0005-string-resources-and-locales.md) | All text in string resources, English and Croatian | accepted |
| [0006](0006-no-secrets-in-git.md) | No secrets in git; signing from an untracked file | accepted |
| [0007](0007-app-name.md) | The app is called Troško | accepted |
| [0008](0008-application-id.md) | Application id `dev.cirimo.trosko` | accepted |
| [0009](0009-min-sdk-26.md) | Minimum SDK 26 | accepted |
| [0010](0010-quality-tooling.md) | Spotless with ktlint, detekt, strict Lint, one check command | accepted |
| [0011](0011-provisional-placeholders.md) | Provisional placeholders awaiting the architecture and design sessions | accepted; backup superseded by 0020 |
| [0012](0012-layers-and-state-flow.md) | Three layers and one-way state flow | accepted |
| [0013](0013-module-layout.md) | Four modules, features as packages | accepted |
| [0014](0014-manual-dependency-injection.md) | Manual dependency injection | accepted |
| [0015](0015-navigation-3.md) | Navigation 3 | accepted |
| [0016](0016-room-3-and-migrations.md) | Room 3 on bundled SQLite, with hand-written migrations | accepted |
| [0017](0017-money-type.md) | Money is integer minor units with a currency | accepted |
| [0018](0018-domain-model-and-identifiers.md) | One record model, categories with kinds, UUID keys | accepted |
| [0019](0019-dates-and-periods.md) | Dates are local dates, periods are half-open ranges | accepted |
| [0020](0020-android-backup.md) | Android backup: device transfer always, cloud only when encrypted | accepted |
| [0021](0021-export-and-restore.md) | Export as CSV and JSON, restore from JSON | accepted |
| [0022](0022-no-app-level-encryption.md) | No encryption of our own | accepted |
| [0023](0023-no-network-and-no-crash-sdk.md) | No network access, no crash reporting SDK | accepted |
| [0024](0024-testing-and-performance-guardrails.md) | Testing per layer, Compose rules and performance guardrails | accepted |
| [0025](0025-csv-follows-the-app-language.md) | The CSV export follows the app language | accepted |
