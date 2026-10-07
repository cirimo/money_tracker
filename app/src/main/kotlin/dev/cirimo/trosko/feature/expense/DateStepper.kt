package dev.cirimo.trosko.feature.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.InkGlyph
import dev.cirimo.trosko.designsystem.component.QuietIconButton
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.dayLabelText

/**
 * Changes the date one day at a time. Almost every expense is from today or yesterday, so two
 * arrows are faster than a calendar. The forward arrow is disabled on today.
 */
@Composable
fun DateStepper(
    day: DayLabel,
    canStepForward: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dayText = dayLabelText(day)
    val dayDescription = stringResource(R.string.expense_entry_date_description, dayText)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuietIconButton(
            glyph = InkGlyph.ChevronLeft,
            contentDescription = stringResource(R.string.expense_entry_day_back_description),
            onClick = onBack,
        )
        TroskoText(
            text = dayText,
            // Announced when it changes, because the arrows that change it say nothing new.
            modifier =
                Modifier.semantics {
                    contentDescription = dayDescription
                    liveRegion = LiveRegionMode.Polite
                },
            style = TroskoTheme.typography.label,
        )
        QuietIconButton(
            glyph = InkGlyph.ChevronRight,
            contentDescription = stringResource(R.string.expense_entry_day_forward_description),
            onClick = onForward,
            enabled = canStepForward,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DateStepperPreview() {
    TroskoTheme {
        DateStepper(day = DayLabel.Yesterday, canStepForward = true, onBack = {}, onForward = {})
    }
}
