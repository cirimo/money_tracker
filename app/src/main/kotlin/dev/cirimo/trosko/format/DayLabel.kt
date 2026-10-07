package dev.cirimo.trosko.format

import java.time.LocalDate

/** How a date is named on screen: by a word when it is close to today, otherwise by the date. */
sealed interface DayLabel {
    data object Today : DayLabel

    data object Yesterday : DayLabel

    data class On(
        val date: LocalDate,
    ) : DayLabel

    companion object {
        /** Names [date] as seen from [today], which the caller takes from the injected clock. */
        fun of(
            date: LocalDate,
            today: LocalDate,
        ): DayLabel =
            when (date) {
                today -> Today
                today.minusDays(1) -> Yesterday
                else -> On(date)
            }
    }
}
