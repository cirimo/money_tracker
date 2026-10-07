---
paths:
  - "**/src/main/res/**"
  - "**/src/main/AndroidManifest.xml"
---

# Android resources and the manifest

The principles behind these rules are in
[docs/CONVENTIONS.md](../../docs/CONVENTIONS.md#strings). This file covers the mechanics.

## Strings

- English lives in `values/strings.xml` and is the default. Croatian lives in
  `values-hr/strings.xml`. Add a string to both in the same commit; Lint fails the build on a
  missing translation and on a translation whose default was removed.
- Keys are snake case and describe place and purpose: `<screen or feature>_<element>_<role>`,
  for example `expense_form_amount_label`. Keep related keys together and in the same order in
  both files, so the two can be compared by eye.
- Use positional placeholders (`%1$s`, `%2$d`) even when there is only one, because Croatian
  word order often differs from English.
- Use `<plurals>` for anything counted. Croatian needs the `one`, `few` and `other` forms.
- Escape apostrophes (`\'`) and use real typographic characters where the language calls for
  them. Croatian text must use proper diacritics (č, ć, đ, š, ž); files are UTF-8.
- Mark strings that must not be translated with `translatable="false"`. The app name is one.
- Amounts, dates and numbers are never baked into strings. They are formatted for the locale
  in code and passed in as arguments.
- Write Croatian as a native speaker would, not as a word-for-word translation, and follow the
  tone of voice in [docs/DESIGN.md](../../docs/DESIGN.md) once it is defined. If you are unsure
  of a Croatian phrasing, say so and let the owner check it.

## Adding a language

Add the `values-<lang>` folder and list the language in `localeFilters` in
`app/build.gradle.kts`. The locale config for the per-app language picker is generated from
those folders. `res/resources.properties` names the default locale and does not change.

## Other resources

- Colours, dimensions, styles, drawables and fonts are design decisions. While
  [docs/DESIGN.md](../../docs/DESIGN.md) is a stub, do not add any. The current theme and
  launcher icon are placeholders.
- The minimum SDK is 26, so resource folders qualified `-v26` or lower are redundant and Lint
  rejects them. Adaptive icons go in `mipmap-anydpi`.
- Prefer vector drawables. Do not add large bitmaps without discussing the size cost.
- After moving or renaming a resource folder, run a clean build; the incremental resource
  merge can report the resource as missing.

## Manifest

- Adding a permission, an exported component, an intent filter or a new `<application>`
  attribute affects the Play Store listing and the data safety form. Ask the owner first and
  update [docs/RELEASE.md](../../docs/RELEASE.md).
- Backup is switched off in three places that must agree: `android:allowBackup`,
  `xml/data_extraction_rules.xml` and `xml/full_backup_content.xml`. Changing it is an
  architecture decision; see [docs/ARCHITECTURE.md](../../docs/ARCHITECTURE.md).
