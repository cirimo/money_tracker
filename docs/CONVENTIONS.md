# Conventions

How code in this project is written. Every rule comes with its reason, so that when you meet a
case the rule does not mention you can work out what it would say. If a rule's reason no longer
holds, raise it with the owner and change the rule here rather than quietly ignoring it.

Formatting is not described in this document. Spotless and ktlint own it, configured in
[.editorconfig](../.editorconfig), and `.\gradlew.bat spotlessApply` fixes it. Arguing with the
formatter by hand is wasted time.

This document does not describe layers, modules, state holders, dependency injection or
navigation. Those belong to [ARCHITECTURE.md](ARCHITECTURE.md); only the coding rules that
follow from them are here. It also says nothing
about colours, type or motion values, which belong to [DESIGN.md](DESIGN.md).

## Money

Money is never stored, passed around or computed as `Float` or `Double`, not even as an
intermediate value. Binary floating point cannot represent most decimal fractions exactly:
`0.1 + 0.2` is not `0.3`. The errors are tiny, but they accumulate across sums, they make equality
checks unreliable, and a finance app that shows a total one cent off has lost the user's trust
for good. See [decision 0004](decisions/0004-money-is-never-floating-point.md).

What follows from that:

- Do not call `toDouble()`, `toFloat()`, `toDoubleOrNull()` or similar on anything that is or
  will become an amount, including text the user typed. Parse into an exact type.
- An amount always travels with its currency. A bare number is not money, and adding two bare
  numbers that happen to be in different currencies is a silent bug.
- Rounding is always explicit, with a named rounding mode, at a point chosen on purpose. Division
  (splitting, averages, percentages) is where precision is lost, so that is where to be careful.
- Formatting an amount for display goes through locale-aware formatting, never string
  concatenation or `"%.2f"`. Decimal separators, grouping and the position of the currency symbol
  differ between English and Croatian.
- Animating an amount on screen (a counter rolling up, for example) may use floating point for
  the animation progress, but the value displayed at rest must come from the exact amount, and
  the float must never flow back into data.
- Percentages and ratios derived from money for charts may be floating point, because they are
  drawing instructions and not money. Never convert them back into an amount.

The one type that represents an amount is `Money` in `:core:domain`; what it allows and why is
in [ARCHITECTURE.md](ARCHITECTURE.md#money). Do not pass an amount around as a bare `Long`
outside the database layer, and do not add a second money type.

## Strings

No user-facing text is written in Kotlin or in layout code. Every string the user can see or a
screen reader can speak comes from string resources. English in `values/` is the default and
Croatian in `values-hr/` is the second language. See
[decision 0005](decisions/0005-string-resources-and-locales.md).

The reason is that retrofitting localisation is slow and error-prone: hardcoded strings hide in
content descriptions, error messages and formatted sentences, and the app is going to ship in two
languages from its first release. Doing it from the first screen costs almost nothing.

- Add every new string to both languages in the same commit. Lint fails the build on a missing
  translation, which is intended.
- Never build a sentence by concatenating pieces. Word order differs between languages. Use one
  string with positional placeholders (`%1$s`).
- Use plurals resources for anything counted. Croatian has more plural forms than English, so
  `if (count == 1)` is wrong.
- Name keys by where and what, in snake case: `expense_form_amount_label`, not `label1` and not
  the English text. A key that restates its text becomes a lie when the text changes.
- Things that are not translated (the app name, format patterns) are marked
  `translatable="false"`.
- Text that only developers see (log messages, exception messages, test names) is plain English
  in code.

Details on the resource files themselves are in
[.claude/rules/resources.md](../.claude/rules/resources.md).

## Kotlin

- Prefer `val`, immutable collections and data that cannot be in an invalid state. Most bugs in
  UI code come from state changing somewhere unexpected.
- Make illegal states unrepresentable with types rather than checking for them at runtime. A
  sealed type with three cases is better than three nullable fields of which exactly one is set.
- Keep visibility as narrow as it will go: `private` by default, `internal` when a file is not
  enough. A small public surface is what makes later refactoring cheap.
- Do not use `!!`. It turns a design question (can this be null?) into a crash in the user's
  hands. If the value truly cannot be null, restructure so the type says so, or use
  `requireNotNull` with a message that explains the broken assumption.
- No magic numbers in logic. Name the constant, so the reader learns what it means and there is
  one place to change it.
- Compiler warnings fail the build. A warning that lingers gets ignored, and then the one that
  matters is lost among the rest. Fix the cause. If suppression is truly right, suppress at the
  narrowest scope and say why in a comment.
- Comments explain why, never what. If code needs a comment to explain what it does, rename or
  restructure it. Public declarations whose contract is not obvious from the signature get KDoc.
- Do not leave commented-out code or TODOs without an owner. Put open work in
  [STATUS.md](STATUS.md), where the next session will see it.

## Compose

- A composable that emits UI is a noun in PascalCase and returns `Unit`. A composable that
  returns a value is camelCase like any function. This is the platform convention and the
  tooling assumes it.
- Every composable that emits UI takes `modifier: Modifier = Modifier` as its first optional
  parameter and applies it to its root element exactly once. Callers need to position and size
  what you give them, and a composable that ignores its modifier cannot be reused.
- Hoist state. A composable receives the values it shows and lambdas for the events it raises,
  and does not reach out for data itself. This is what makes it previewable, testable and
  reusable. The state lives in the screen's view model; the four parts of a screen are
  described in [ARCHITECTURE.md](ARCHITECTURE.md#the-ui-layer).
- Wrap what a screen draws in `TroskoTheme`, and build it from the design system's components
  once they exist. A feature never uses a UI library directly.
- Keep composables free of business logic. They decide how something looks, not what is true.
  Anything involving money, dates or rules is computed elsewhere and passed in ready to show.
- Do not read or write anything slow during composition, and do not create objects in
  composition that should survive it without `remember`. Composition can run on every frame of
  an animation, and this app animates a lot.
- Give every screen and every non-trivial component a `@Preview`. Previews are private functions
  named after what they preview with a `Preview` suffix.
- No hardcoded colours, text styles, shapes, spacing or durations once the design system exists.
  They come from the theme defined in [DESIGN.md](DESIGN.md). Until then, the placeholder screen
  is intentionally unstyled.
- Animation is a first-class part of this product, so treat it as real code: no arbitrary
  delays, no animation that blocks input, and every one honours the reduced-motion setting.

## Naming and file organisation

- The root package is `dev.cirimo.trosko`. Sources live under `src/main/kotlin`, tests under
  `src/test/kotlin`, instrumented tests under `src/androidTest/kotlin`.
- One top-level public declaration per file, and the file is named after it. A reader looking
  for `ExpenseForm` should find `ExpenseForm.kt` without searching. Small private helpers and
  the preview live in the same file as the thing they serve.
- Names say what something is in the language of the product (expense, income, category), not
  how it is implemented. Avoid `Manager`, `Helper`, `Util` and `Data` as name parts; they are a
  sign that the responsibility has not been thought through.
- Booleans read as statements: `isSaving`, `hasNote`. Event lambdas are `onSomething`.
- Which module and package a file belongs in is set out in
  [ARCHITECTURE.md](ARCHITECTURE.md#modules). Each module's root package is
  `dev.cirimo.trosko` followed by the module's name, except `:app`, which uses the root itself.
- Everything in `:core:domain` is immutable: `val` properties and read-only collections only.
  The Compose compiler is told to trust this, so a mutable type there causes stale UI.
- In `:core:data`, everything except `Repositories` is `internal`. If the app needs something
  from the data layer, it gets it through a repository interface declared in the domain.
- The current date and time come from a `java.time.Clock` that is passed in. Calling
  `LocalDate.now()` or `Instant.now()` without one makes the code untestable around midnight
  and month ends, which is exactly where this app's logic lives.
- A value stored in the database as text that stands for an enum goes through an explicit
  mapping, as in `RecordKindColumn.kt`, never through the enum's `name`.

## Testing

- Logic gets a JVM unit test in `src/test`. These are fast, run in `check` and in CI, and are
  where most tests belong. Anything that touches money gets tests for the awkward cases: zero,
  negative amounts, large amounts, rounding boundaries, and both locales when formatting.
- Behaviour that needs a device (Compose UI, anything touching Android APIs) gets an
  instrumented test in `src/androidTest`. `check` compiles these but cannot run them, so run
  `connectedDebugAndroidTest` on a device when you change UI behaviour, and say in your report
  whether you did.
- Test behaviour, not implementation. A test that breaks when you rename a private function is
  measuring the wrong thing.
- A bug fix comes with a test that fails without the fix. Otherwise the bug returns.
- Test names describe the behaviour in a sentence. Unit tests may use backticked names;
  instrumented tests may not, because Android's runtime rejects them on older API levels.
- Tests never depend on the device language. Read expected text from resources.
- Which layer is tested where is in [ARCHITECTURE.md](ARCHITECTURE.md#testing). Fakes of
  repository interfaces are written by hand; there is no mocking library.

More detail is in [.claude/rules/tests.md](../.claude/rules/tests.md).

## Accessibility baseline

Fun is for everyone ([PRODUCT.md](PRODUCT.md), principle 6), and Google Play reviews
accessibility too. Every screen meets this baseline before it counts as done:

- Every interactive element has a touch target of at least 48 by 48 dp.
- Every meaningful image or icon has a content description from string resources; purely
  decorative ones are explicitly marked as such with `null`.
- Nothing is communicated by colour alone. Income versus expense, for example, needs a sign,
  a label or an icon as well.
- Text scales with the system font size and the layout survives at 200 percent.
- Text and essential graphics meet WCAG AA contrast.
- Custom-drawn and animated components expose proper semantics, so TalkBack can read and operate
  them. This matters more here than in most apps, because much of the UI will be custom.
- Motion honours the system's remove-animations setting, and nothing flashes rapidly.

## Error handling

- Never swallow an exception. An empty `catch` block hides the one piece of information needed
  to fix the problem. Either handle it meaningfully or let it propagate.
- Do not catch `Exception` or `Throwable` broadly. Catch what you expect and can handle.
  In coroutine code, catching broadly also swallows cancellation, which breaks structured
  concurrency in ways that are hard to see.
- Expected failures (invalid input, nothing found) are part of the normal flow and are modelled
  as values, not thrown. Exceptions are for things that should not happen.
- The user never sees a raw exception message or a stack trace. Messages shown to the user come
  from string resources, say what happened in plain language, and say what they can do next, in
  the app's own voice.
- Losing a user's financial record is the worst thing this app can do. Any failure while saving
  must leave the data either fully saved or untouched, and must tell the user which.
- Never log amounts, notes or anything else the user entered. Logs end up in bug reports and
  crash tools, and this is private financial data.

## Dependencies

Each dependency is code we did not write but must ship, keep updated and answer for on Google
Play. Add one only when it clearly earns its place, ask the owner first, and record significant
ones as a decision. The mechanics are in [.claude/rules/gradle.md](../.claude/rules/gradle.md).
