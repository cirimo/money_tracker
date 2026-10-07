package dev.cirimo.trosko.domain.money

/** What reading an amount from text came to. A failure is a value, never an exception. */
sealed interface ParsedAmount {
    /** The text is an amount. It may be zero; whether zero is acceptable is the caller's rule. */
    data class Valid(
        val money: Money,
    ) : ParsedAmount

    /** There was nothing to read. */
    data object Empty : ParsedAmount

    /** The text is not a plain decimal number: a sign, a letter, or more than one separator. */
    data object NotANumber : ParsedAmount

    /** More digits after the separator than the currency has, for example 1,234 in euros. */
    data object TooManyDecimals : ParsedAmount

    /** The amount does not fit in the range [Money] can hold. */
    data object TooLarge : ParsedAmount
}
