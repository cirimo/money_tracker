package dev.cirimo.trosko.feature.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.DayCell
import dev.cirimo.trosko.designsystem.component.InkGlyph
import dev.cirimo.trosko.designsystem.component.QuietIconButton
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.period.CalendarMonth
import dev.cirimo.trosko.format.currentLocale
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.WeekFields

private const val DAYS_IN_WEEK = 7

/**
 * A page of a calendar to choose a day from, for a date further back than the arrows of the
 * [DateStepper] are good for. It opens on the month of the chosen day and can be turned back
 * month by month, and forward as far as the month of [days]' latest day.
 *
 * Which month is shown is this composable's own business, like a scroll position: it changes
 * nothing about the record until a day is chosen.
 */
@Composable
internal fun DayPicker(
    days: DayPickerDays,
    onDayPick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = currentLocale()
    // Kept as text, which is what a saved state can hold.
    var shownMonth by rememberSaveable { mutableStateOf(YearMonth.from(days.selected).toString()) }
    val month = remember(shownMonth) { YearMonth.parse(shownMonth) }
    val page = remember(month, locale) { CalendarMonth(month, WeekFields.of(locale).firstDayOfWeek) }
    val monthPattern = stringResource(R.string.month_pattern)
    val monthName = remember(locale, monthPattern) { DateTimeFormatter.ofPattern(monthPattern, locale) }
    val fullDate = remember(locale) { DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            QuietIconButton(
                glyph = InkGlyph.ChevronLeft,
                contentDescription = stringResource(R.string.expense_calendar_month_back_description),
                onClick = { shownMonth = month.minusMonths(1).toString() },
            )
            TroskoText(
                text = monthName.format(month),
                // Announced when it changes, because the arrows that change it say nothing new.
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = TroskoTheme.typography.heading,
            )
            QuietIconButton(
                glyph = InkGlyph.ChevronRight,
                contentDescription = stringResource(R.string.expense_calendar_month_forward_description),
                onClick = { shownMonth = month.plusMonths(1).toString() },
                enabled = month < YearMonth.from(days.latest),
            )
        }
        // Seven touch targets side by side are a little wider than a narrow phone's content, so
        // the page may reach into the gutter by the few points it needs.
        Column(
            modifier = Modifier.fillMaxWidth().requiredWidthIn(min = TroskoDimens.MinTouchTarget * DAYS_IN_WEEK),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceXs),
        ) {
            // The day cells say their weekday themselves, so the header is for the eye only.
            Row(modifier = Modifier.clearAndSetSemantics {}) {
                page.weekdays.forEach { weekday ->
                    TroskoText(
                        text = weekday.getDisplayName(TextStyle.SHORT, locale),
                        modifier = Modifier.weight(1f),
                        style = TroskoTheme.typography.caption.copy(textAlign = TextAlign.Center),
                        color = TroskoTheme.colors.inkSoft,
                    )
                }
            }
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
            ) {
                page.weeks.forEach { week ->
                    Row {
                        week.forEach { day ->
                            if (day == null) {
                                Row(modifier = Modifier.weight(1f)) {}
                            } else {
                                DayCell(
                                    text = day.dayOfMonth.toString(),
                                    contentDescription = fullDate.format(day),
                                    selected = day == days.selected,
                                    onClick = { onDayPick(day) },
                                    // Room on the side the shadow is not, so the second outline
                                    // of the chosen day clears its neighbour.
                                    modifier = Modifier.weight(1f).padding(start = TroskoDimens.SpaceS),
                                    enabled = day <= days.latest,
                                    underlined = day == days.today,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DayPickerPreview() {
    val today = LocalDate.of(2026, 10, 7)
    TroskoTheme {
        DayPicker(days = DayPickerDays(selected = today.minusDays(1), today = today, latest = today), onDayPick = {})
    }
}
