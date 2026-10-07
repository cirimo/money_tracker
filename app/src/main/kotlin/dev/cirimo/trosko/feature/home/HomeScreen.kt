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
import androidx.compose.ui.unit.IntOffset
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.TroskoButton
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoMotion
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.format.LatestHeading
import dev.cirimo.trosko.format.RecordLineSticker

/**
 * Where the app starts. Until the overview of a period exists, it shows the one thing that is
 * true: what was written down last. Pressing a record opens it to be corrected. The way to a
 * new expense sits low, under the thumb.
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNewExpense: () -> Unit,
    onRecordClick: (RecordId) -> Unit,
    onLand: () -> Unit,
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
            LatestHeading(notice = (uiState as? HomeUiState.Loaded)?.notice)
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
                        LatestRecords(uiState = uiState, onRecordClick = onRecordClick, onLand = onLand)
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

@Composable
private fun LatestRecords(
    uiState: HomeUiState.Loaded,
    onRecordClick: (RecordId) -> Unit,
    onLand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // When a record is deleted the ones under it move up into its place. Nothing fades.
    val placement = if (TroskoTheme.reducedMotion) null else TroskoMotion.settle<IntOffset>()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
        // Room at the top for a sticker that drops in larger than life.
        contentPadding = PaddingValues(top = TroskoDimens.SpaceXs, bottom = TroskoDimens.SpaceS),
    ) {
        items(uiState.latest, key = { it.id.value }) { line ->
            RecordLineSticker(
                line = line,
                isLanded = line.id == uiState.landedId,
                isLandingPending = uiState.isLandingPending,
                onLand = onLand,
                onClick = { onRecordClick(line.id) },
                modifier = Modifier.animateItem(fadeInSpec = null, placementSpec = placement, fadeOutSpec = null),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 600, widthDp = 412)
@Composable
private fun HomeScreenPreview() {
    TroskoTheme {
        HomeScreen(
            uiState = HomeUiState.Loaded(latest = emptyList()),
            onNewExpense = {},
            onRecordClick = {},
            onLand = {},
        )
    }
}
