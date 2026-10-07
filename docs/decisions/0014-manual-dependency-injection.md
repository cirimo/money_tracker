# 0014. Manual dependency injection

- **Status**: accepted
- **Date**: 2026-10-07, architecture session, approved by the owner

## Context

The object graph is about ten objects: a database, a few repositories, settings, and the view
models that use them. The owner left this choice to the session.

## Decision

No dependency injection framework. Classes take their dependencies through constructors, and
`AppContainer` in `:app` is the single place that creates and wires the long-lived ones.

Hilt was the close alternative. It supports the current toolchain, but it adds a Gradle plugin,
an annotation processor and a bytecode transform to every build, for a graph this small.

## Consequences

- No generated code and nothing to learn; a missing dependency is a compile error at the call.
- Wiring is written by hand, which stays pleasant only while the graph is small.
- Everything is already constructor-injected, so adopting Hilt later is mechanical: add
  annotations and delete `AppContainer`.
