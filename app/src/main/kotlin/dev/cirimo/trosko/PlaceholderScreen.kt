package dev.cirimo.trosko

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

/**
 * Proves the toolchain end to end and nothing more. It is unstyled on purpose:
 * the theme and the first real screen come from the design and architecture sessions.
 */
@Composable
fun PlaceholderScreen(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = stringResource(R.string.placeholder_message))
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    PlaceholderScreen()
}
