package dev.cirimo.trosko.format

import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.domain.money.Money
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Turns amounts into text for one locale. The separators, the currency symbol and where it
 * stands all come from the platform's formatter, never from a pattern of ours, and the number
 * handed to it is always exact.
 */
class MoneyFormatter(
    private val locale: Locale,
) {
    /** The key that separates euros from cents, as this locale writes it. */
    val decimalSeparator: String
        get() = DecimalFormatSymbols.getInstance(locale).monetaryDecimalSeparator.toString()

    /** A finished amount with all its decimals, such as `12,50 €`. */
    fun format(money: Money): String = formatterFor(money.currency).format(money.toBigDecimal())

    /**
     * An amount that is still being typed. It shows exactly the decimals typed so far, and the
     * separator as soon as it has been pressed, so `12`, `12,` and `12,5` all look different.
     */
    fun formatTyping(input: AmountInput): String {
        val formatter = formatterFor(input.currency)
        val typedDecimals = input.fraction?.length ?: 0
        formatter.minimumFractionDigits = typedDecimals
        formatter.maximumFractionDigits = typedDecimals
        if (formatter is DecimalFormat) {
            formatter.isDecimalSeparatorAlwaysShown = input.fraction != null
        }
        return formatter.format(input.toMoney().toBigDecimal())
    }

    // A new formatter every time: NumberFormat is not safe to share between threads.
    private fun formatterFor(currency: Currency): NumberFormat =
        NumberFormat.getCurrencyInstance(locale).also { it.currency = currency }
}
