package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * One day on a page of a calendar. A day that can be chosen is a small sticker; the chosen one
 * lies flat with a second outline and says so to a screen reader. A day that cannot be chosen
 * is only its number in soft ink, and is not offered to a screen reader at all. Put the days of
 * one choice inside a parent marked `selectableGroup`.
 *
 * @param text the number of the day.
 * @param contentDescription the whole date in words, which is what a screen reader says.
 * @param underlined marks the day apart from the choice, for example as today.
 */
@Composable
fun DayCell(
    text: String,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    underlined: Boolean = false,
) {
    val style =
        TroskoTheme.typography.label.copy(
            color = if (enabled) TroskoTheme.colors.ink else TroskoTheme.colors.inkSoft,
            textDecoration = if (underlined) TextDecoration.Underline else null,
        )
    // The number stays whole on one line and shrinks with enlarged text instead of breaking.
    val number: @Composable () -> Unit = {
        BasicText(
            text = text,
            style = style,
            maxLines = 1,
            softWrap = false,
            autoSize =
                TextAutoSize.StepBased(
                    minFontSize = smallestFittingSize(),
                    maxFontSize = TroskoTheme.typography.label.fontSize,
                ),
        )
    }

    if (enabled) {
        PressableSticker(
            fill = TroskoTheme.colors.card,
            onClick = onClick,
            modifier = modifier.semantics { this.contentDescription = contentDescription },
            shape = TroskoTheme.shapes.chip,
            selected = selected,
        ) { number() }
    } else {
        Box(
            modifier =
                modifier
                    .defaultMinSize(minWidth = TroskoDimens.MinTouchTarget, minHeight = TroskoDimens.MinTouchTarget)
                    .clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
        ) { number() }
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DayCellPreview() {
    ThemePreview {
        Row {
            DayCell(text = "5", contentDescription = "5. listopada", selected = false, onClick = {})
            DayCell(text = "6", contentDescription = "6. listopada", selected = true, onClick = {})
            DayCell(text = "7", contentDescription = "7. listopada", selected = false, onClick = {}, underlined = true)
            DayCell(text = "8", contentDescription = "8. listopada", selected = false, onClick = {}, enabled = false)
        }
    }
}
