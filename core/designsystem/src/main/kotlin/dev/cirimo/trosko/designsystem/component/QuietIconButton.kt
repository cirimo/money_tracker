package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * A [QuietButton] that shows an icon instead of a word. The icon stands alone here, so
 * [contentDescription] is required: it is what a screen reader says.
 */
@Composable
fun QuietIconButton(
    glyph: InkGlyph,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    PressableSticker(fill = TroskoTheme.colors.card, onClick = onClick, modifier = modifier, enabled = enabled) {
        InkIcon(
            glyph = glyph,
            contentDescription = contentDescription,
            tint = if (enabled) TroskoTheme.colors.ink else TroskoTheme.colors.inkSoft,
        )
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun QuietIconButtonPreview() {
    ThemePreview {
        QuietIconButton(glyph = InkGlyph.Backspace, contentDescription = "Obriši znamenku", onClick = {})
        QuietIconButton(
            glyph = InkGlyph.ChevronRight,
            contentDescription = "Dan kasnije",
            onClick = {},
            enabled = false,
        )
    }
}
