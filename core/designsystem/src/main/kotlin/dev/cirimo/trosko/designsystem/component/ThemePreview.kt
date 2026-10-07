package dev.cirimo.trosko.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/** The frame every component preview is drawn in: the theme, the paper and some room. */
@Composable
internal fun ThemePreview(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    TroskoTheme {
        Column(
            modifier = modifier.background(TroskoTheme.colors.paper).padding(TroskoDimens.SpaceL),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
            content = content,
        )
    }
}
