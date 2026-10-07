package dev.cirimo.trosko.format

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DayLabelTest {
    private val today = LocalDate.of(2026, 10, 1)

    @Test
    fun `the same day is today`() {
        assertEquals(DayLabel.Today, DayLabel.of(today, today))
    }

    @Test
    fun `the day before is yesterday, across the end of a month too`() {
        assertEquals(DayLabel.Yesterday, DayLabel.of(LocalDate.of(2026, 9, 30), today))
    }

    @Test
    fun `two days back is named by its date`() {
        val date = LocalDate.of(2026, 9, 29)

        assertEquals(DayLabel.On(date), DayLabel.of(date, today))
    }

    @Test
    fun `a day after today is named by its date, not guessed at`() {
        val tomorrow = today.plusDays(1)

        assertEquals(DayLabel.On(tomorrow), DayLabel.of(tomorrow, today))
    }
}
