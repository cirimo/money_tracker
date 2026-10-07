package dev.cirimo.trosko.feature.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.StickerSurface
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * Proves the app end to end and nothing more: the message appears once storage has answered.
 * It is deleted when the first real feature lands.
 */
@Composable
fun PlaceholderScreen(
    uiState: PlaceholderUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(TroskoTheme.colors.paper)
                .safeDrawingPadding()
                .padding(TroskoDimens.ScreenGutter),
        contentAlignment = Alignment.Center,
    ) {
        // While storage has not answered, the page stays blank.
        if (uiState == PlaceholderUiState.Ready) {
            StickerSurface(fill = TroskoTheme.colors.yellow) {
                TroskoText(
                    text = stringResource(R.string.placeholder_message),
                    modifier = Modifier.padding(TroskoDimens.SpaceXl),
                    style = TroskoTheme.typography.heading.copy(textAlign = TextAlign.Center),
                    color = TroskoTheme.colors.onSticker,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    TroskoTheme {
        PlaceholderScreen(uiState = PlaceholderUiState.Ready)
    }
}
