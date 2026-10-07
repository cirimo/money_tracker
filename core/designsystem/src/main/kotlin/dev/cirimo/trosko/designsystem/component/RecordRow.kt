package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

private const val LARGE_TEXT_SCALE = 1.3f

/**
 * One record on the page: the category's icon on its colour, the category and a second line
 * (the date, a note) on one side, and the amount on the other. A screen reader hears the whole
 * row as one sentence.
 *
 * With [onClick] the row is a sticker to press, which opens the record: it goes flat under the
 * finger like a button, is announced as one, and shows keyboard focus.
 *
 * @param amount already formatted for the locale, sign included.
 * @param fill the category's sticker colour, used for the icon's badge.
 */
@Composable
fun RecordRow(
    title: String,
    subtitle: String,
    amount: String,
    glyph: InkGlyph,
    fill: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    // With enlarged text the amount no longer fits beside the name without breaking a word, so
    // it moves to a line of its own under it.
    val isTextLarge = LocalDensity.current.fontScale > LARGE_TEXT_SCALE

    val content: @Composable BoxScope.() -> Unit = {
        Row(
            modifier = Modifier.padding(horizontal = TroskoDimens.SpaceM, vertical = TroskoDimens.SpaceS),
            horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // The shadow padding of the badge is given back, so the badge itself is BadgeSize.
            StickerSurface(
                fill = fill,
                modifier = Modifier.size(TroskoDimens.BadgeSize + TroskoDimens.ShadowOffset),
                shape = TroskoTheme.shapes.chip,
                lift = { 0f },
            ) {
                InkIcon(glyph = glyph, contentDescription = null, tint = TroskoTheme.colors.onSticker)
            }
            Column(modifier = Modifier.weight(1f)) {
                TroskoText(text = title, style = TroskoTheme.typography.label)
                TroskoText(text = subtitle, style = TroskoTheme.typography.caption, color = TroskoTheme.colors.inkSoft)
                if (isTextLarge) TroskoText(text = amount, style = TroskoTheme.typography.label)
            }
            if (!isTextLarge) TroskoText(text = amount, style = TroskoTheme.typography.label)
        }
    }

    if (onClick == null) {
        StickerSurface(
            fill = TroskoTheme.colors.card,
            modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
            contentAlignment = Alignment.CenterStart,
            content = content,
        )
    } else {
        // Being clickable already makes the row one sentence to a screen reader.
        PressableSticker(
            fill = TroskoTheme.colors.card,
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
            content = content,
        )
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RecordRowPreview() {
    ThemePreview {
        RecordRow("Namirnice", "Danas · kruh i mlijeko", "−12,50 €", InkGlyph.Basket, TroskoTheme.colors.green)
        RecordRow("Režije", "pon 5. 10.", "−61,37 €", InkGlyph.Bolt, TroskoTheme.colors.yellow, onClick = {})
    }
}
