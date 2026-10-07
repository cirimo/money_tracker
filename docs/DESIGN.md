# Design

How Troško looks, moves and speaks. Read
[PRODUCT.md](PRODUCT.md#the-principle-fun-and-not-like-other-finance-apps) first: it says why
the app must not look like a finance app, and this document is the means of meeting it. The
choices here were made with the owner, who picked the direction from drawn alternatives; the
records are [decisions 0026 to 0029](decisions/README.md).

The exact values (colour codes, sizes, spring constants) live in code, in
`core/designsystem/.../theme/`, and are not repeated here. This document says what each one is
for and how to use it. [STATUS.md](STATUS.md) says which components have been built.

## The idea: a notebook, a fat marker and stickers

Troško is a paper notebook kept by someone who enjoys keeping it. Everything on screen is one of
three things: the **paper** it is drawn on, **ink** from a thick marker, or a **sticker** stuck
on the page. A record is a sticker. A category is a sticker. A button is a sticker you press
flat.

In words: handmade, chunky, cheerful, direct. It is not cute or childish, not sarcastic, and not
polished to the point of looking generated. When a screen feels too clean, it has drifted.

Four rules produce the look, and a screen that follows them belongs:

1. **Every raised shape has an ink outline and a hard shadow.** The shadow is a solid copy of the
   shape, offset down and to the right, with no blur. There is no other kind of elevation.
2. **Pressing closes the shadow.** The shape moves into the shadow's place. Pressed, selected
   and disabled all mean "flat on the page".
3. **Corners are uneven.** Each shape has four slightly different corner radii, so outlines look
   drawn, not generated.
4. **Colour belongs to stickers.** The page and the text are paper and ink. Colour appears only
   as the fill of an outlined shape.

## Colour

Tokens: `TroskoColors`, read as `TroskoTheme.colors`.

| Token | Role |
|---|---|
| `paper` | The page. The background of every screen. |
| `card` | A neutral raised surface, such as a row in a list. |
| `ink` | Text, icons and every outline. |
| `inkSoft` | Secondary text only. Never an outline, never the only carrier of meaning. |
| `shadow` | The hard shadow. |
| `onSticker` | Text and icons on any sticker colour. Dark in both themes. |
| `yellow` | Troško's own colour: the mascot and the main action of a screen. |
| `pink`, `cyan`, `green`, `orange`, `violet` | Category colours, together with yellow. |

- **Two themes, light and dark, following the system.** There is no setting for it in the app.
  In the dark theme the page is dark, ink is light, and the sticker colours are a little deeper;
  text on a sticker stays dark.
- **Dynamic colour is ignored.** The palette is the identity; a wallpaper must not repaint it.
- **A sticker colour never appears without its ink outline.** On light paper the fills alone are
  far too pale to be seen as shapes (contrast against paper is between 1.3 and 2.1 to 1). The
  outline is what makes them visible, which is why rule 1 has no exceptions.
- **Nothing is said by colour alone.** A category has a colour, an icon and a name. Income and
  expense differ by sign, by word and by icon. In charts, income is a solid fill and expense is
  hatched.
- **There is no red.** The app never scolds, and red reads as a scolding. An error is said in
  words, with the mascot's surprised face, in ink on paper.
- **One filled yellow button per screen**, for the main action. Other actions use `card`.

Measured contrast, on 2026-10-07: ink on paper is 17.9 to 1 in light and 15.2 in dark; soft ink
on paper is 7.1 and 7.5; dark text on the sticker colours is between 8.5 and 14.1 in light and
between 5.8 and 9.2 in dark. All pass WCAG AA for text. Measure again when a colour changes.

## Typography

Tokens: `TroskoTypography`, read as `TroskoTheme.typography`.

Two typefaces, both bundled in the app (it has no network access) and both under the SIL Open
Font License 1.1, whose texts are in `core/designsystem/licenses/`:

- **Shantell Sans**, a hand-drawn face, for everything with character: amounts, titles, buttons,
  stickers, row text. Its digits are all the same width, so amounts line up in a column.
- **Nunito**, a plain rounded face, for running text and captions, where handwriting tires.

| Style | Use |
|---|---|
| `amount` | The one large amount on the entry screen. |
| `title` | A screen title or a headline total. |
| `heading` | A section heading. |
| `label` | Buttons, stickers, amounts in lists. |
| `note` | Row text and short notes. |
| `body` | Running text, in Nunito. |
| `caption` | The smallest text, in Nunito. |

- An amount is always in Shantell Sans, never in Nunito, so that digits are tabular.
- Amounts are formatted for the locale ([CONVENTIONS.md](CONVENTIONS.md#money)): the currency
  symbol and separators come from the formatter, never from the layout.
- Text scales with the system font size. A layout must survive 200 percent: rows wrap, and the
  large amount may shrink to fit its box but never below the `title` size.
- A single word on a button or a chip is never broken across lines. When it does not fit it
  shrinks, at most down to the `caption` size as it looks without enlargement.
- Shantell Sans has "bounce" and "informality" axes that make letters jump. They are left at
  zero. Text that wobbles is harder to read, and numbers must stay still.

## Shape, outline and shadow

Tokens: `TroskoShapes` and `TroskoDimens`. The building block is the `StickerSurface`
composable; do not draw outlines and shadows by hand elsewhere.

- Two shapes: `sticker` for buttons, cards and rows, `chip` for small pieces. Use the tokens; do
  not invent a third radius.
- The outline width and the shadow offset are fixed (`Outline`, `ShadowOffset`). The shadow
  offset is also how far a press travels.
- A disabled control has a dashed outline, no shadow and paper fill. It does not rely on a faded
  colour.
- **Tilt.** A sticker may sit a few degrees off level: the large amount on the entry screen and
  the one record that has just landed. That record stays tilted until the next one lands, then
  straightens. Every other row of a list, and all running text, stay level.
- **A second outline** just outside a shape says one of two things: solid, this is the chosen
  one of a group; dashed, this has keyboard focus.

## Spacing and layout

- Spacing comes from the scale in `TroskoDimens` (`SpaceXs` to `SpaceXxl`) and from nowhere else.
- Content is `ScreenGutter` from the screen edges and draws edge to edge under the system bars,
  with insets applied to content, not to the page colour.
- Everything tappable is at least `MinTouchTarget` in both directions.
- The app is designed for a phone held upright in one hand: the main action sits low, within
  reach of a thumb. On tablets, unfolded foldables and in landscape, content keeps a single
  column no wider than `MaxContentWidth`, centred on the paper. There is no separate large-screen
  layout.

## Icons

Icons are drawn for this app, as vectors in code, in the same hand as everything else: a single
stroke of ink with round ends and round joins, no fills, on a 24 unit grid. There is no icon
library and so no licence to track. Each category has one icon; the set grows as categories
need it. An icon never appears alone where it carries meaning: it sits next to its word, or has a
content description.

## Troško, the mascot

Troško is a worn coin with a face: slightly lopsided, one eye bigger than the other, one eyebrow
permanently raised, thin arms and legs. His rim is the same hard shadow every button has, so he
looks cut from the same notebook. He is yellow, and yellow in the app is his colour.

- He appears in empty states, in the launcher icon, and now and then beside the reward after a
  save. He is not on screen all the time.
- He has four expressions: neutral (looking at you sideways), delighted (arms up, after a
  save), dozing (an empty period) and startled (something went wrong).
- **He never comments on how much the user spends.** He reacts to the act of writing something
  down, not to the amount.

The owner approved the sketches in [design/mascot/](design/mascot/README.md); they fix the
character. No mascot artwork is in the app yet. Final vector artwork drawn from those sketches,
the launcher icon (with its adaptive and monochrome variants) and the splash screen are open
work.

## Charts

Charts are drawn with Compose's own drawing APIs, in the same language as everything else.

- **Spending by category** is a stack of horizontal bars, one per category, each an outlined
  shape filled with the category's colour, with the category's name on one side and the exact
  amount on the other. The longest bar is the largest category.
- **Month over month** is pairs of upright bars: income solid, expense hatched with ink lines.
- Every bar has its number written next to it. A chart is never the only place a figure appears.
- A chart exposes its values to TalkBack as a list of labelled figures, not as an image.

## Motion

Tokens: `TroskoMotion`. Things move on springs, not timed curves, so any animation can be
interrupted by the next touch without a jump.

| Spec | For |
|---|---|
| `press` | A shape going flat under a finger and coming back. Fast, no bounce. |
| `pop` | Something landing on the page, such as a saved record. One visible bounce. |
| `settle` | Something moving to a new place without calling attention. |
| `reduced` | What every animation becomes under reduced motion: a short fade. |

Principles:

- **Motion answers a touch; it never makes the user wait.** No animation blocks input, and the
  next action is possible while the previous one is still settling.
- **Things have weight.** They are pressed, stuck, peeled and dropped. Nothing fades in from
  nowhere or slides on rails.
- **Short.** A response is over in well under half a second.
- **A little different each time.** Where an interaction repeats many times a day, something
  small varies, so the hundredth time is not a recording of the first.

### The signature interaction: saving a record

1. The button goes flat the instant it is touched (`press`).
2. The record lands at the top of the list as a sticker, dropping in slightly too large and
   tilted, and settling with one bounce (`pop`). Its final tilt is random within a few degrees.
   The `LandingSticker` component does this.
3. The entry form is already empty and ready. The user can type the next amount while the
   sticker is still landing.

Now and then, not every time, Troško shows his delighted face beside the landing sticker. How
often is to be tuned on a real device; it must stay a small surprise.

### Screen transitions

Moving between screens turns the page: the new screen comes in from the side with a slight
tilt that straightens as it lands (`settle`). Going back reverses it and follows the predictive
back gesture.

Built so far: the new page slides in over the old one on `settle` and back out again, with a
cross-fade under reduced motion. The tilt is not built, and nobody has watched the transition
follow a back gesture.

### Reduced motion

`TroskoTheme.reducedMotion` is true when the user has switched animations off in the system.
Then nothing translates, scales or rotates: a press changes state at once, a saved record and a
new screen fade in with `reduced`, and tilts are dropped. Every animated component must handle
this, and a component that ignores it is not finished.

Two limits of how this is built. The setting is read when the activity starts, so a change
shows after the app is reopened. And when the system's animator scale is zero, Compose itself
finishes every animation at once, so the `reduced` fade is in practice an immediate change.

## Haptics and sound

There are none. The owner decided the app does not vibrate and makes no sound, so there is no
setting for either. Feedback is entirely visual, which is why the press and the landing sticker
have to be unmistakable.

## Voice

Troško speaks like a friend who keeps the notebook with you: short, plain, on your side.

- In Croatian it says **ti**, never vi.
- It is short. A confirmation is one word if one word will do.
- It states facts and never judges them. It reports what was spent; it does not say whether that
  was a lot.
- It has a little humour in empty states and none in errors.
- No exclamation marks in system text, no "successfully", no "please", no "are you sure".
- An error says what happened, that nothing was lost (when that is true), and what to do next.

| Situation | Croatian | English | Not like this |
|---|---|---|---|
| Saved | Zapisano. | Noted. | Vaš trošak je uspješno spremljen! |
| Empty month | Još ništa ovaj mjesec. Prvi zapis? | Nothing yet this month. First entry? | Nema podataka za prikaz. |
| Save failed | Nije se zapisalo. Ništa nije izgubljeno, probaj opet. | That didn't save. Nothing was lost, try again. | Greška: SQLiteException |
| A month's total | Listopad dosad: 884,60 €. | October so far: €884.60. | Ups, opet si pretjerao s kavom! |
| Deleting | Obrisati ovaj zapis? | Delete this entry? | Jeste li sigurni da želite nastaviti? |

The main verb of the app is **zapisati** in Croatian and **note** in English: the user writes
things down in a notebook, they do not "submit" or "save" them.

## Components

Built on `StickerSurface`, in `core/designsystem/.../component/`. A feature uses these and never
draws its own outline, shadow or text colour.

| Component | States | Notes |
|---|---|---|
| `StickerSurface` | resting, pressed (via `lift`), dashed | The raised shape. Everything else is built on it. |
| `TroskoText` | | Text in the theme's ink. Use it instead of `BasicText`. |
| `TroskoButton` | default, pressed, disabled, focused | Yellow fill. One per screen. |
| `QuietButton`, `QuietIconButton` | default, pressed, disabled, focused | The same on `card`, for secondary actions and for the keys of the keypad. The icon one requires a content description. |
| `CategoryChip` | default, selected, focused | Sticker colour, icon and name. Selected is flat with a second outline. |
| `TroskoTextField` | empty, focused, filled | An ink line under the text, no box: dashed at rest, solid with focus. The error state is not built. |
| `RecordRow` | default | `card` fill; the icon on its colour, category and date on one side, amount on the other. With enlarged text the amount moves under the name. The pressed state is not built; nothing opens a record yet. |
| `AmountDisplay` | | The large tilted amount on the entry screen. It takes the colour of the chosen category, and `card` while none is chosen. |
| `InkIcon` | | One icon from `InkGlyph`, drawn as a single stroke. |
| `LandingSticker` | landing, at rest | The landing of something just saved; see the signature interaction. |
| Bars | | The two chart kinds above. Not built. |
| Empty state | | Troško dozing, one line of text, one button. Not built. |

Naming: a component of the design system is prefixed `Trosko` when its plain name would collide
with a Compose or platform name (`TroskoButton`, `TroskoText`), and otherwise named for what it
is in the notebook (`StickerSurface`).

A component is finished when it has a preview in both themes, honours reduced motion, has
correct semantics for TalkBack (role, state, label), is at least `MinTouchTarget` and shows
keyboard focus. Keyboard focus is shown as a second, dashed ink outline outside the shape.

The amount keypad and the date stepper are not components of the design system. They are
arrangements of these components that belong to recording an expense, and live in that feature.

## Accessibility beyond the baseline

The baseline is in [CONVENTIONS.md](CONVENTIONS.md#accessibility-baseline). What this design
adds:

- Custom shapes get their meaning from semantics, never from how they are drawn. A chip that
  looks pressed must also say "selected".
- A tilted element reads and is focused as if it were level; tilt is decoration only.
- The landing sticker after a save is announced to TalkBack as a short confirmation, since the
  animation itself says nothing to someone who cannot see it.
- Hatching, not colour, separates expense from income in charts.

## Not built on Material

The app uses Compose's foundation layer and no Material library. Every default Material
component fails the screenshot test in [PRODUCT.md](PRODUCT.md), and its model of elevation,
ripple and colour roles is the opposite of outline, hard shadow and press-flat. The cost is that
buttons, fields, sheets and the rest are written here, including their accessibility. See
[decision 0027](decisions/0027-no-material-library.md).
