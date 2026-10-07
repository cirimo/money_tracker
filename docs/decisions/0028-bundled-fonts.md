# 0028. Shantell Sans and Nunito, bundled

- **Status**: accepted
- **Date**: 2026-10-07, design session, decided with the owner

## Context

The design needs a hand-drawn typeface for amounts and labels and a plain one for small text.
The app has no network access, so fonts cannot be downloaded at run time. Checked on
2026-10-07: both fonts are under the SIL Open Font License 1.1, both cover the Latin Extended
characters Croatian needs, and Shantell Sans documents its digits as tabular.

## Decision

Shantell Sans and Nunito are shipped as variable font files in
`core/designsystem/src/main/res/font`, taken from the Google Fonts repository. The owner agreed
to adding them. Their licence texts are kept in `core/designsystem/licenses`.

## Consequences

- The fonts add about 0.8 MB to the release APK, measured: 5.79 MB before, 6.57 MB after. Most
  of it is Shantell Sans, which carries Cyrillic and four variation axes the app does not use.
  Subsetting it would win most of that back and has not been done.
- The app must show the fonts' licence notices; that is on the list in
  [RELEASE.md](../RELEASE.md).
- Amounts are always set in Shantell Sans. Whether Nunito has tabular digits was not
  established, so it must not be used for amounts.
