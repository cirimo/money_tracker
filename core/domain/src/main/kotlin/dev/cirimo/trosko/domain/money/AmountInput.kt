package dev.cirimo.trosko.domain.money

import java.util.Currency

/**
 * An amount while it is being typed on the app's own keypad, one key at a time. It holds the
 * digits as text, so `12,` and `12,5` are different states even though they are the same amount.
 *
 * @property whole the digits before the separator, without leading zeros; empty when none.
 * @property fraction the digits after the separator, or null while the separator has not been
 * pressed.
 */
data class AmountInput(
    val currency: Currency,
    val whole: String = "",
    val fraction: String? = null,
) {
    private val fractionDigits: Int get() = currency.defaultFractionDigits.coerceAtLeast(0)

    val isEmpty: Boolean get() = whole.isEmpty() && fraction == null

    /** What the input is after [key] was pressed. */
    fun press(key: AmountKey): AmountInput =
        when (key) {
            is AmountKey.Digit -> digit(key.value)
            AmountKey.Separator -> separator()
            AmountKey.Backspace -> backspace()
        }

    /** The key for [digit], from 0 to 9, was pressed. A key that would not fit changes nothing. */
    fun digit(digit: Int): AmountInput {
        require(digit in 0..MAX_DIGIT) { "Not a single digit: $digit" }
        return when {
            fraction != null -> if (fraction.length < fractionDigits) copy(fraction = fraction + digit) else this
            whole.isEmpty() && digit == 0 -> this
            whole.length < MAX_WHOLE_DIGITS -> copy(whole = whole + digit)
            else -> this
        }
    }

    /** The decimal separator key was pressed. */
    fun separator(): AmountInput = if (fraction == null && fractionDigits > 0) copy(fraction = "") else this

    /** The backspace key was pressed: the last thing typed is taken back. */
    fun backspace(): AmountInput =
        when {
            fraction == null -> copy(whole = whole.dropLast(1))
            fraction.isEmpty() -> copy(fraction = null)
            else -> copy(fraction = fraction.dropLast(1))
        }

    /** The amount typed so far. Nothing typed is zero. */
    fun toMoney(): Money =
        when (val parsed = parseAmount("${whole.ifEmpty { "0" }}.${fraction.orEmpty()}", currency)) {
            is ParsedAmount.Valid -> parsed.money
            else -> error("The keypad let through an amount that cannot be read: $parsed")
        }

    companion object {
        /** Seven digits before the separator: up to 9 999 999,99. More is a typing slip. */
        const val MAX_WHOLE_DIGITS = 7
        private const val MAX_DIGIT = 9
        private const val DECIMAL_BASE = 10L

        /**
         * The keys that would have typed [money], for correcting an amount already written down.
         * A round amount comes back without decimals, as someone would type it; any other comes
         * back with all of them, so 12,50 is not shown as 12,5.
         */
        fun of(money: Money): AmountInput {
            require(!money.isNegative) { "An amount on the keypad is never negative" }
            val fractionDigits = money.currency.defaultFractionDigits.coerceAtLeast(0)
            var unitsPerWhole = 1L
            repeat(fractionDigits) { unitsPerWhole *= DECIMAL_BASE }
            val whole = money.minorUnits / unitsPerWhole
            val fraction = money.minorUnits % unitsPerWhole
            return AmountInput(
                currency = money.currency,
                whole = if (whole == 0L) "" else whole.toString(),
                fraction = if (fraction == 0L) null else fraction.toString().padStart(fractionDigits, '0'),
            )
        }
    }
}
