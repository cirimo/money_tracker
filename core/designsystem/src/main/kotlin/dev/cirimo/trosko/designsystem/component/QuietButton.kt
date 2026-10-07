package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * A button for everything that is not the main action of the screen: the same sticker as
 * [TroskoButton], on the neutral card colour.
 *
 * @param glyph an icon drawn before the word. The word names the button, so the icon is
 * decoration to a screen reader.
 */
@Composable
fun QuietButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = TroskoTheme.typography.label,
    glyph: InkGlyph? = null,
) {
    val ink = if (enabled) TroskoTheme.colors.ink else TroskoTheme.colors.inkSoft

    PressableSticker(fill = TroskoTheme.colors.card, onClick = onClick, modifier = modifier, enabled = enabled) {
        Row(
            modifier = Modifier.padding(horizontal = TroskoDimens.SpaceM, vertical = TroskoDimens.SpaceS),
            horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceXs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (glyph != null) InkIcon(glyph = glyph, contentDescription = null, tint = ink)
            TroskoText(text = text, style = style, color = ink)
        }
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun QuietButtonPreview() {
    ThemePreview {
        QuietButton(text = "7", onClick = {}, style = TroskoTheme.typography.title)
        QuietButton(text = "Odustani", onClick = {})
        QuietButton(text = "Obriši", onClick = {}, glyph = InkGlyph.Trash)
        QuietButton(text = "Odustani", onClick = {}, enabled = false)
    }
}
