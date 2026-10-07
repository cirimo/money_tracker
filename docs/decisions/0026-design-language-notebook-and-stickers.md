# 0026. The design language: notebook, marker and stickers

- **Status**: accepted
- **Date**: 2026-10-07, design session, decided with the owner

## Context

The product must not look like a finance app ([PRODUCT.md](../PRODUCT.md)). The owner was
shown four broad directions and chose the hand-drawn notebook, then three renderings of it on
the same screen, and chose the one with a fat marker and stickers.

## Decision

The whole UI is paper, ink and stickers: every raised shape has an ink outline and a hard
offset shadow, pressing closes the shadow, corners are uneven, and colour appears only as the
fill of an outlined shape. Light and dark themes follow the system, with no setting in the app.
Dynamic colour is ignored. There is no red. The language is described in full in
[DESIGN.md](../DESIGN.md).

The owner also decided: Croatian text addresses the user as "ti", in a short and relaxed
voice; and Troško is a discreet mascot, a coin with a face, present in empty states, the icon
and occasional rewards.

The rejected renderings were squared paper with ballpoint ink (calm, but one ink colour cannot
tell categories apart) and a dotted sketchbook with pencil lines and a rubber stamp (the
quietest, but thin grey lines read worst in sunlight and in the dark theme).

## Consequences

- The look is cheap to render: flat fills and solid shapes, no blur and no shaders.
- Sticker colours are too pale to be seen on light paper without their outline, so the outline
  rule has no exceptions.
- Nothing can lean on platform conventions for free; see
  [0027](0027-no-material-library.md).
- The mascot's final artwork, the launcher icon and the splash screen are still to be made.
