package dev.cirimo.trosko.domain.period

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

/**
 * One month laid out as a page of a calendar: a column for each day of the week, a row for each
 * week. Which day the week starts on differs between countries, so it is passed in.
 */
data class CalendarMonth(
    val month: YearMonth,
    val firstDayOfWeek: DayOfWeek,
) {
    /** The days of the week in the order of the columns. */
    val weekdays: List<DayOfWeek>
        get() = (0 until DAYS_IN_WEEK).map { firstDayOfWeek.plus(it.toLong()) }

    /**
     * The rows of the page, seven places each. A place is null where that column belongs to the
     * month before or after.
     */
    val weeks: List<List<LocalDate?>>
        get() {
            val firstColumn = (month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + DAYS_IN_WEEK) % DAYS_IN_WEEK
            val days = (1..month.lengthOfMonth()).map { month.atDay(it) }
            val places = List<LocalDate?>(firstColumn) { null } + days
            return places.chunked(DAYS_IN_WEEK) { week -> week + List<LocalDate?>(DAYS_IN_WEEK - week.size) { null } }
        }

    private companion object {
        const val DAYS_IN_WEEK = 7
    }
}
