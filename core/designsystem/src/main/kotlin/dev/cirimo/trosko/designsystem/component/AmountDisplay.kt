package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

private const val TILT_DEGREES = -2f

/**
 * The one large amount on the entry screen, on a sticker that sits a little off level. The text
 * shrinks to stay on one line as the amount grows or the system font gets larger, but never
 * below the title size.
 *
 * @param text the amount, already formatted for the locale.
 * @param fill a sticker colour, typically that of the chosen category; null for the neutral
 * card while nothing is chosen.
 */
@Composable
fun AmountDisplay(
    text: String,
    modifier: Modifier = Modifier,
    fill: Color? = null,
) {
    val reducedMotion = TroskoTheme.reducedMotion
    val style = TroskoTheme.typography.amount

    StickerSurface(
        fill = fill ?: TroskoTheme.colors.card,
        // Tilt is decoration: under reduced motion nothing sits rotated.
        modifier = modifier.graphicsLayer { rotationZ = if (reducedMotion) 0f else TILT_DEGREES },
        contentAlignment = Alignment.CenterEnd,
    ) {
        BasicText(
            text = text,
            modifier = Modifier.padding(horizontal = TroskoDimens.SpaceL, vertical = TroskoDimens.SpaceS),
            style =
                style.copy(
                    color = if (fill == null) TroskoTheme.colors.ink else TroskoTheme.colors.onSticker,
                    textAlign = TextAlign.End,
                ),
            maxLines = 1,
            autoSize =
                TextAutoSize.StepBased(
                    minFontSize = TroskoTheme.typography.title.fontSize,
                    maxFontSize = style.fontSize,
                ),
        )
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AmountDisplayPreview() {
    ThemePreview {
        AmountDisplay(text = "0 €")
        AmountDisplay(text = "12,50 €", fill = TroskoTheme.colors.green)
        AmountDisplay(text = "9.999.999,99 €", fill = TroskoTheme.colors.pink)
    }
}
