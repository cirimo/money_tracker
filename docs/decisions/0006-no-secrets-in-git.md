# 0006. No secrets in git; signing from an untracked file

- **Status**: accepted
- **Date**: 2026-10-07, decided by the owner before the bootstrap session; mechanism chosen in
  the bootstrap session

## Context

The repository is hosted on GitHub and its history is permanent. A leaked upload key or
password would let someone else publish as us.

## Decision

No secret, keystore or signing credential is ever committed. The release build reads its
signing configuration from `keystore.properties` in the repository root, which is git-ignored.
A template, `keystore.properties.example`, is tracked. When the real file is absent the release
build type has no signing config and produces an unsigned artefact.

## Consequences

- Fresh clones and CI build debug and release without any secret.
- Whoever makes a real release needs the keystore and the properties file on their machine,
  obtained outside git. The keystore itself should live outside the repository directory.
- If CI is ever to produce signed builds, the secrets come from the CI provider's encrypted
  store.
- A secret committed by accident must be treated as compromised and rotated. Removing it from
  history is not enough, and rewriting pushed history is not allowed anyway.
- The remaining key-management work is tracked in [RELEASE.md](../RELEASE.md).
