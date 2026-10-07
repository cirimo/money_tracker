package dev.cirimo.trosko.feature.expense

import java.time.LocalDate

/**
 * The days a calendar page needs to know about.
 *
 * @property selected the day the record is filed under now.
 * @property today marked on the page so the eye has something to hold on to.
 * @property latest the last day that may be chosen; later days are shown but cannot be pressed.
 */
data class DayPickerDays(
    val selected: LocalDate,
    val today: LocalDate,
    val latest: LocalDate,
)
