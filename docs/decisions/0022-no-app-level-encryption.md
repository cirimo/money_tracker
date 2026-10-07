# 0022. No encryption of our own

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

The data is private financial information. The owner was asked whether the app should encrypt
its database itself.

## Decision

The owner decided no. The app relies on Android's application sandbox and the device's own
storage encryption.

An encrypted database (SQLCipher) was rejected: it adds a large native library and a key whose
loss makes the data unreadable for good, which for this app is a worse outcome than the threat
it guards against.

## Consequences

- On a rooted or compromised device the database file is readable.
- A screen lock for the app itself (biometric or PIN) can be added later without touching the
  database.
- Introducing database encryption later would mean migrating every user's whole database.
