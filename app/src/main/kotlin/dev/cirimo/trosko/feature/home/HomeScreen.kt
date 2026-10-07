package dev.cirimo.trosko.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.TroskoButton
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.format.RecordLineRow

/**
 * Where the app starts. Until the overview of a period exists, it shows the one thing that is
 * true: what was written down last. The way to a new expense sits low, under the thumb.
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNewExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().background(TroskoTheme.colors.paper).safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = TroskoDimens.MaxContentWidth)
                    .padding(horizontal = TroskoDimens.ScreenGutter)
                    .padding(top = TroskoDimens.SpaceL, bottom = TroskoDimens.SpaceL),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
        ) {
            TroskoText(text = stringResource(R.string.home_latest_heading), style = TroskoTheme.typography.heading)
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // While storage has not answered, the page stays blank.
                if (uiState is HomeUiState.Loaded) {
                    if (uiState.latest.isEmpty()) {
                        TroskoText(
                            text = stringResource(R.string.home_empty_message),
                            style = TroskoTheme.typography.note,
                            color = TroskoTheme.colors.inkSoft,
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
                            contentPadding = PaddingValues(bottom = TroskoDimens.SpaceS),
                        ) {
                            items(uiState.latest, key = { it.id.value }) { line -> RecordLineRow(line = line) }
                        }
                    }
                }
            }
            TroskoButton(
                text = stringResource(R.string.home_new_expense_button),
                onClick = onNewExpense,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 600, widthDp = 412)
@Composable
private fun HomeScreenPreview() {
    TroskoTheme {
        HomeScreen(uiState = HomeUiState.Loaded(latest = emptyList()), onNewExpense = {})
    }
}
