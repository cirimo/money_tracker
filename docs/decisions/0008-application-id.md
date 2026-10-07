# 0008. Application id `dev.cirimo.trosko`

- **Status**: accepted
- **Date**: 2026-10-07, proposed in the bootstrap session and confirmed by the owner

## Context

The application id identifies the app on devices and on Google Play. Once an app is published
it can never be changed; a different id is a different app, with no upgrade path for users.
The owner does not own a domain, so a reversed domain name was not available.

## Decision

The application id and the code namespace are both `dev.cirimo.trosko`. `cirimo` is the owner's
GitHub account name and `dev` is a neutral prefix that does not imply ownership of a `.com`
domain.

## Consequences

- This value is permanent. Do not change it, and do not let a refactoring tool change it.
- The debug build type appends `.debug`, giving `dev.cirimo.trosko.debug`, so debug and release
  builds can be installed side by side. The namespace, and therefore the `R` class and the
  package of the source files, stays `dev.cirimo.trosko` in both.
- If the owner later acquires a domain, the id still does not change.
