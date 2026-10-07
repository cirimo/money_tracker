package dev.cirimo.trosko.format

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * The heading over a list of the latest records, with the word about what was just done beside
 * it, or under it when enlarged text needs the room.
 *
 * @param notice null when there is nothing to say.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LatestHeading(
    notice: RecordNotice?,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        TroskoText(text = stringResource(R.string.home_latest_heading), style = TroskoTheme.typography.heading)
        // A landing, or a row leaving, says nothing to someone who cannot see it, so the word is
        // announced.
        Box(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
            if (notice != null) {
                TroskoText(text = recordNoticeText(notice), style = TroskoTheme.typography.note)
            }
        }
    }
}
