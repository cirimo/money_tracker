# 0013. Four modules, features as packages

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

The build supports any number of modules. The question was how many boundaries are worth a
module for a small app with one developer, given that money handling and the database are the
parts that must not be reached into casually.

## Decision

Four modules: `:app`, `:core:domain` (plain Kotlin), `:core:data` and `:core:designsystem`.
Features are packages under `:app/feature/`, not modules. The dependency rules are in
[ARCHITECTURE.md](../ARCHITECTURE.md#modules).

A module per feature was rejected for now: for about six screens it would mean some ten
modules, each with its own resources in two languages, while shared-element transitions cross
feature boundaries anyway. The owner confirmed features as packages.

## Consequences

- The compiler keeps Android, Compose and Room out of the domain, and the database out of the
  UI.
- "Features do not import each other" is a convention the compiler does not check.
- Splitting a feature package out into a module later is cheap. Merging modules back would not
  be.
- A plain Kotlin module needed a `trosko.jvm.library` convention plugin, and Android Lint is
  applied to it so the app's lint run still sees its sources.
