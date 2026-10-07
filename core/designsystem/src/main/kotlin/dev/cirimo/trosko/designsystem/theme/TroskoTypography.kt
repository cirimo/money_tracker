package dev.cirimo.trosko.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.cirimo.trosko.designsystem.R

/**
 * The type scale. The hand-drawn face carries everything with character: amounts, titles,
 * buttons and stickers. The plain face is for small and long text, where handwriting tires.
 * See docs/DESIGN.md, typography.
 */
@Immutable
data class TroskoTypography(
    /** The one large amount on the entry screen. */
    val amount: TextStyle,
    /** A screen title or a total. */
    val title: TextStyle,
    /** A section heading. */
    val heading: TextStyle,
    /** Buttons, stickers and amounts in lists. */
    val label: TextStyle,
    /** Row text and short notes, in the hand-drawn face. */
    val note: TextStyle,
    /** Running text, in the plain face. */
    val body: TextStyle,
    /** The smallest text: captions and hints. */
    val caption: TextStyle,
)

// Both fonts are variable; the weight is picked on the font's own axis.
private fun hand(weight: FontWeight) =
    Font(
        resId = R.font.shantell_sans,
        weight = weight,
        variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
    )

private fun plain(weight: FontWeight) =
    Font(
        resId = R.font.nunito,
        weight = weight,
        variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
    )

private val HandFamily = FontFamily(hand(FontWeight.Medium), hand(FontWeight.ExtraBold))
private val PlainFamily = FontFamily(plain(FontWeight.Normal), plain(FontWeight.SemiBold))

// Digits of equal width, so amounts line up in a column and do not jitter while they change.
private const val TABULAR_FIGURES = "tnum"

internal val DefaultTypography =
    TroskoTypography(
        amount =
            TextStyle(
                fontFamily = HandFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 44.sp,
                lineHeight = 48.sp,
                fontFeatureSettings = TABULAR_FIGURES,
            ),
        title =
            TextStyle(
                fontFamily = HandFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontFeatureSettings = TABULAR_FIGURES,
            ),
        heading =
            TextStyle(
                fontFamily = HandFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                lineHeight = 26.sp,
            ),
        label =
            TextStyle(
                fontFamily = HandFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFeatureSettings = TABULAR_FIGURES,
            ),
        note =
            TextStyle(
                fontFamily = HandFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 22.sp,
            ),
        body =
            TextStyle(
                fontFamily = PlainFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 22.sp,
            ),
        caption =
            TextStyle(
                fontFamily = PlainFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            ),
    )
