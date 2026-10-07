package dev.cirimo.trosko.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Every colour the app may use. Two structural colours, paper and ink, carry the whole UI; the
 * six sticker colours are for categories and for the one main action on a screen.
 * See docs/DESIGN.md, colour.
 */
@Immutable
data class TroskoColors(
    /** The page everything sits on. */
    val paper: Color,
    /** A neutral surface raised above the paper, such as a list row. */
    val card: Color,
    /** Text, outlines and icons. */
    val ink: Color,
    /** Secondary text. Never used for outlines. */
    val inkSoft: Color,
    /** The hard offset shadow under every raised shape. */
    val shadow: Color,
    /** Text and icons on top of any sticker colour. It is dark in both themes. */
    val onSticker: Color,
    val yellow: Color,
    val pink: Color,
    val cyan: Color,
    val green: Color,
    val orange: Color,
    val violet: Color,
)

internal val LightColors =
    TroskoColors(
        paper = Color(0xFFFFFDF6),
        card = Color(0xFFFFFFFF),
        ink = Color(0xFF151515),
        inkSoft = Color(0xFF5B574D),
        shadow = Color(0xFF151515),
        onSticker = Color(0xFF151515),
        yellow = Color(0xFFFFD23F),
        pink = Color(0xFFFF8FAB),
        cyan = Color(0xFF7BDFF2),
        green = Color(0xFFB8F2A0),
        orange = Color(0xFFFFA552),
        violet = Color(0xFFC3A6FF),
    )

internal val DarkColors =
    TroskoColors(
        paper = Color(0xFF1B1A17),
        card = Color(0xFF26241F),
        ink = Color(0xFFF4EFE2),
        inkSoft = Color(0xFFB0AA9B),
        shadow = Color(0xFF000000),
        onSticker = Color(0xFF151515),
        yellow = Color(0xFFE0B21F),
        pink = Color(0xFFE06C8C),
        cyan = Color(0xFF49B9CF),
        green = Color(0xFF86C96C),
        orange = Color(0xFFDD8438),
        violet = Color(0xFFA488E6),
    )
