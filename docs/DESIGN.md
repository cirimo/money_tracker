# Design

**This document is a stub.** It will be written by the session dedicated to the visual and
motion design language. Until then the app has one deliberately unstyled placeholder screen.

If you are not in that session, do not choose colours, fonts, shapes, animation values or a
tone of voice as a side effect of other work. If a task cannot proceed without one, stop and
ask the owner.

## The brief the design must answer

Troško has to feel fun and have a strong personality of its own, clearly unlike a typical
banking or budgeting app. Motion and playful interaction are part of the product from the first
feature, not polish added at the end. The principle is stated concretely, with tests for
settling arguments, in
[PRODUCT.md](PRODUCT.md#the-principle-fun-and-not-like-other-finance-apps). Read it first; the
design language is the means of meeting it.

## What exists now and is only a placeholder

- The window theme is the platform's `Theme.Material.Light.NoActionBar`, light only.
- The screen draws its text with Compose `BasicText` and no theme. No Material library is on
  the classpath, on purpose: whether to build on Material 3, on parts of it, or on a fully
  custom system is a design decision.
- The launcher icon is a black disc on white.
- The app name "Troško" is decided. Its visual treatment is not.

## What the design session must define

Identity:

- The personality in words: a few adjectives, what it is and what it is not, and reference
  points. Whether "Troško" is a character or mascot, and if so how present it is.
- Tone of voice for all text in both English and Croatian: how the app addresses the user
  (including formal or informal address in Croatian), how it phrases amounts, empty states,
  errors and confirmations, and how it stays on the user's side without scolding. Include
  examples of right and wrong.

Visual language:

- Colour: the palette, semantic roles (including income and expense without relying on colour
  alone), light and dark themes, whether Android's dynamic colour is used or deliberately
  ignored, and contrast targets.
- Typography: typefaces and their licences for distribution in an app, the type scale, and how
  numbers and amounts are set (tabular figures, emphasis, currency symbols).
- Shape: corner treatment, the overall geometry, elevation or its absence.
- Spacing and layout: the grid, spacing scale, and behaviour on small phones, large phones,
  landscape, foldables and tablets.
- Iconography: style, source and licence, and how category icons work.
- Illustration and imagery, if any.
- The launcher icon, including adaptive and monochrome (themed) variants, and the splash
  screen.
- Data visualisation: how charts and breakdowns look and move, and how they stay readable and
  accessible.

Motion and feedback:

- Motion principles: what moves, why, and how it should feel. Easing and spring
  specifications, a duration scale, and choreography rules for sequences.
- The signature interactions, above all the one for recording an expense, which is the most
  repeated action in the app and has to stay enjoyable on the hundredth use.
- Screen transitions and shared-element behaviour.
- Reduced motion: what each animation becomes when the user has asked for less.
- Haptics: when the device vibrates, with which patterns, and how it degrades on devices with
  weak haptic hardware.
- Sound: whether the app makes sound at all, when, how it respects silent mode, and the user
  setting that controls it.
- Performance budget: animations must hold the display's refresh rate on a mid-range phone.

System:

- The component set: which components exist, their states (default, pressed, disabled, loading,
  error, empty) and how they are named in code.
- How the design is expressed in code: the theme object, design tokens and their naming.
  This must agree with [ARCHITECTURE.md](ARCHITECTURE.md).
- Accessibility specifics beyond the baseline in
  [CONVENTIONS.md](CONVENTIONS.md#accessibility-baseline): how custom-drawn, animated components
  stay operable with TalkBack and at large font sizes.

## When this document is written

Replace this stub entirely. The finished document should let a new session build a screen that
looks and feels like it belongs, without asking what colour or duration to use. Record each
significant choice in [decisions/](decisions/README.md), move coding rules that follow into
[CONVENTIONS.md](CONVENTIONS.md), and update the store-listing items in
[RELEASE.md](RELEASE.md) that depend on the identity.
