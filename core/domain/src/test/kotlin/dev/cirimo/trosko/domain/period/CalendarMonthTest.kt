package dev.cirimo.trosko.domain.period

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.YearMonth

class CalendarMonthTest {
    private fun CalendarMonth.dayNumbers(): List<List<Int?>> = weeks.map { week -> week.map { it?.dayOfMonth } }

    @Test
    fun `a month that starts on a Thursday leaves three places empty when weeks start on Monday`() {
        val october = CalendarMonth(YearMonth.of(2026, 10), DayOfWeek.MONDAY)

        assertEquals(listOf(null, null, null, 1, 2, 3, 4), october.dayNumbers().first())
        assertEquals(listOf(26, 27, 28, 29, 30, 31, null), october.dayNumbers().last())
        assertEquals(5, october.weeks.size)
    }

    @Test
    fun `the same month leaves four places empty when weeks start on Sunday`() {
        val october = CalendarMonth(YearMonth.of(2026, 10), DayOfWeek.SUNDAY)

        assertEquals(listOf(null, null, null, null, 1, 2, 3), october.dayNumbers().first())
        assertEquals(DayOfWeek.SUNDAY, october.weekdays.first())
        assertEquals(DayOfWeek.SATURDAY, october.weekdays.last())
    }

    @Test
    fun `a month that starts on the last day of the week has six empty places first`() {
        val february = CalendarMonth(YearMonth.of(2026, 2), DayOfWeek.MONDAY)

        assertEquals(listOf(null, null, null, null, null, null, 1), february.dayNumbers().first())
        assertEquals(5, february.weeks.size)
    }

    @Test
    fun `a February of four full weeks has no empty place`() {
        val february = CalendarMonth(YearMonth.of(2027, 2), DayOfWeek.MONDAY)

        assertEquals(4, february.weeks.size)
        assertTrue(february.weeks.flatten().none { it == null })
    }

    @Test
    fun `a leap February has its twenty-ninth day and every row has seven places`() {
        val february = CalendarMonth(YearMonth.of(2028, 2), DayOfWeek.MONDAY)

        assertEquals(
            29,
            february.weeks
                .flatten()
                .filterNotNull()
                .size,
        )
        assertTrue(february.weeks.all { it.size == 7 })
    }

    @Test
    fun `a month that needs six rows gets them`() {
        val august = CalendarMonth(YearMonth.of(2026, 8), DayOfWeek.MONDAY)

        assertEquals(6, august.weeks.size)
        assertEquals(listOf(31, null, null, null, null, null, null), august.dayNumbers().last())
    }
}
