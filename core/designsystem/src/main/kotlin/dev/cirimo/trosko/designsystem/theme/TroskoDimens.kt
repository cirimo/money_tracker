package dev.cirimo.trosko.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * Sizes that hold everywhere. Spacing comes from this scale and from nowhere else.
 * See docs/DESIGN.md, spacing and shape.
 */
object TroskoDimens {
    val SpaceXs = 4.dp
    val SpaceS = 8.dp
    val SpaceM = 12.dp
    val SpaceL = 16.dp
    val SpaceXl = 24.dp
    val SpaceXxl = 32.dp

    /** The distance from the screen edge to content. */
    val ScreenGutter = SpaceL

    /** The ink outline around every raised shape. */
    val Outline = 3.dp

    /** How far the hard shadow sits from its shape, and so how far a press travels. */
    val ShadowOffset = 4.dp

    /** The smallest size anything tappable may have, in both directions. */
    val MinTouchTarget = 48.dp

    /** The height of a main button. */
    val ButtonHeight = 52.dp

    /** The height of one key on the amount keypad. */
    val KeyHeight = 54.dp

    /** The box an icon is drawn in. Icons are designed on a grid of 24 units. */
    val IconSize = 24.dp

    /** The small coloured sticker that carries a category's icon in a record row. */
    val BadgeSize = 40.dp

    /** The space between a shape and its second outline (selected, or keyboard focus). */
    val SecondOutlineGap = 2.dp

    /** Content never grows wider than this, on tablets, foldables and in landscape. */
    val MaxContentWidth = 480.dp
}
