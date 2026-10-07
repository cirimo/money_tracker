package dev.cirimo.trosko.domain.money

/** One key of the amount keypad. */
sealed interface AmountKey {
    /** @property value from 0 to 9. */
    data class Digit(
        val value: Int,
    ) : AmountKey

    data object Separator : AmountKey

    data object Backspace : AmountKey
}
