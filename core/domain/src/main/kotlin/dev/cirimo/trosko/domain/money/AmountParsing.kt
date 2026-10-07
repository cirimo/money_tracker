package dev.cirimo.trosko.domain.money

import java.util.Currency

/**
 * Reads an amount the user typed, such as `12,50`, straight into the currency's smallest unit.
 * The text never passes through a floating-point number, so nothing is rounded.
 *
 * Either a comma or a dot is accepted as the decimal separator, whatever the language, because
 * people type both. Grouping separators and signs are not accepted.
 */
fun parseAmount(
    text: String,
    currency: Currency,
): ParsedAmount {
    val trimmed = text.trim()
    val parts = trimmed.split(',', '.')
    val whole = parts[0]
    val fraction = parts.getOrElse(1) { "" }
    val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)

    return when {
        trimmed.isEmpty() -> ParsedAmount.Empty
        parts.size > 2 || !(whole + fraction).all { it in '0'..'9' } -> ParsedAmount.NotANumber
        whole.isEmpty() && fraction.isEmpty() -> ParsedAmount.NotANumber
        fraction.length > fractionDigits -> ParsedAmount.TooManyDecimals
        else -> minorUnits(whole, fraction.padEnd(fractionDigits, '0'), currency)
    }
}

private fun minorUnits(
    whole: String,
    paddedFraction: String,
    currency: Currency,
): ParsedAmount {
    val digits = (whole + paddedFraction).trimStart('0').ifEmpty { "0" }
    // Only digits are left, so the one way this can fail is by not fitting in a Long.
    val minorUnits = digits.toLongOrNull() ?: return ParsedAmount.TooLarge
    return ParsedAmount.Valid(Money(minorUnits, currency))
}
