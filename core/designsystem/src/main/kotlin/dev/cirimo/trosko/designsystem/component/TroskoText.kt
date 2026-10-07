package dev.cirimo.trosko.designsystem.component

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * Text in the theme's ink. Use this instead of `BasicText`, whose default colour is black and
 * so disappears on the dark theme's paper.
 */
@Composable
fun TroskoText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TroskoTheme.typography.body,
    color: Color = TroskoTheme.colors.ink,
) {
    BasicText(text = text, modifier = modifier, style = style.copy(color = color))
}
