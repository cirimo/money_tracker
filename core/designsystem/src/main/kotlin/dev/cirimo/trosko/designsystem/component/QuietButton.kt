package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * A button for everything that is not the main action of the screen: the same sticker as
 * [TroskoButton], on the neutral card colour.
 */
@Composable
fun QuietButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = TroskoTheme.typography.label,
) {
    PressableSticker(fill = TroskoTheme.colors.card, onClick = onClick, modifier = modifier, enabled = enabled) {
        TroskoText(
            text = text,
            modifier = Modifier.padding(horizontal = TroskoDimens.SpaceM, vertical = TroskoDimens.SpaceS),
            style = style,
            color = if (enabled) TroskoTheme.colors.ink else TroskoTheme.colors.inkSoft,
        )
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun QuietButtonPreview() {
    ThemePreview {
        QuietButton(text = "7", onClick = {}, style = TroskoTheme.typography.title)
        QuietButton(text = "Odustani", onClick = {})
        QuietButton(text = "Odustani", onClick = {}, enabled = false)
    }
}
