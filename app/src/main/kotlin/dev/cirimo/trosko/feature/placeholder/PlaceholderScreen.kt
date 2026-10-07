package dev.cirimo.trosko.feature.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.R

/**
 * Proves the app end to end and nothing more: the message appears once storage has answered.
 * It is unstyled on purpose; the theme and the first real screen come from the design session.
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
                .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            PlaceholderUiState.Loading -> Unit
            PlaceholderUiState.Ready -> BasicText(text = stringResource(R.string.placeholder_message))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    PlaceholderScreen(uiState = PlaceholderUiState.Ready)
}
