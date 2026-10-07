package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * A category as a sticker to choose: its colour, its icon and its name, never the colour alone.
 * The chosen chip lies flat with a second outline, and says so to a screen reader. Put the chips
 * of one choice inside a parent marked `selectableGroup`.
 *
 * @param fill one of the sticker colours of the theme.
 */
@Composable
fun CategoryChip(
    name: String,
    glyph: InkGlyph,
    fill: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PressableSticker(
        fill = fill,
        onClick = onClick,
        modifier = modifier,
        shape = TroskoTheme.shapes.chip,
        selected = selected,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = TroskoDimens.SpaceS, vertical = TroskoDimens.SpaceS),
            horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceXs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // The name is right beside it, so the icon is decoration to a screen reader.
            InkIcon(glyph = glyph, contentDescription = null, tint = TroskoTheme.colors.onSticker)
            // A name stays whole on one line: it shrinks to fit a narrow chip instead of breaking
            // in the middle of a word.
            BasicText(
                text = name,
                style = TroskoTheme.typography.label.copy(color = TroskoTheme.colors.onSticker),
                maxLines = 1,
                softWrap = false,
                autoSize =
                    TextAutoSize.StepBased(
                        minFontSize = TroskoTheme.typography.caption.fontSize,
                        maxFontSize = TroskoTheme.typography.label.fontSize,
                    ),
            )
        }
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CategoryChipPreview() {
    ThemePreview {
        CategoryChip("Namirnice", InkGlyph.Basket, TroskoTheme.colors.green, selected = false, onClick = {})
        CategoryChip("Prijevoz", InkGlyph.Bus, TroskoTheme.colors.cyan, selected = true, onClick = {})
    }
}
