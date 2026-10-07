package dev.cirimo.trosko.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * The main button. It goes down under the finger the moment it is touched: the shadow closes
 * and the button moves into its place. A disabled button has a dashed outline and no shadow, so
 * its state does not depend on a faded colour.
 *
 * Use one filled button per screen for the main action.
 */
@Composable
fun TroskoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    PressableSticker(
        fill = TroskoTheme.colors.yellow,
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = TroskoDimens.ButtonHeight),
        enabled = enabled,
    ) {
        // The word stays whole on one line: on a narrow button, or with enlarged text, it shrinks
        // to fit instead of breaking in the middle.
        BasicText(
            text = text,
            modifier = Modifier.padding(horizontal = TroskoDimens.SpaceM, vertical = TroskoDimens.SpaceM),
            style =
                TroskoTheme.typography.label.copy(
                    color = if (enabled) TroskoTheme.colors.onSticker else TroskoTheme.colors.inkSoft,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
            softWrap = false,
            autoSize =
                TextAutoSize.StepBased(
                    minFontSize = smallestFittingSize(),
                    maxFontSize = TroskoTheme.typography.label.fontSize,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TroskoButtonPreview() {
    TroskoTheme {
        Column(
            modifier = Modifier.padding(TroskoDimens.SpaceL),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
        ) {
            TroskoButton(text = "Zapiši", onClick = {})
            TroskoButton(text = "Zapiši", onClick = {}, enabled = false)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1B1A17)
@Composable
private fun TroskoButtonDarkPreview() {
    TroskoTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(TroskoDimens.SpaceL),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
        ) {
            TroskoButton(text = "Zapiši", onClick = {})
            TroskoButton(text = "Zapiši", onClick = {}, enabled = false)
        }
    }
}
