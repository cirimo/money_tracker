package dev.cirimo.trosko.domain.money

import java.math.BigDecimal
import java.util.Currency

/**
 * An exact amount of money: a whole number of the currency's smallest unit (cents for the euro)
 * together with the currency it is in.
 *
 * There is deliberately no division and no conversion to or from floating point. Arithmetic that
 * would overflow throws instead of wrapping around, and combining two currencies throws instead
 * of producing a number that means nothing. See docs/ARCHITECTURE.md, money.
 */
data class Money(
    val minorUnits: Long,
    val currency: Currency,
) : Comparable<Money> {
    val isZero: Boolean get() = minorUnits == 0L
    val isPositive: Boolean get() = minorUnits > 0L
    val isNegative: Boolean get() = minorUnits < 0L

    operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return copy(minorUnits = Math.addExact(minorUnits, other.minorUnits))
    }

    operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return copy(minorUnits = Math.subtractExact(minorUnits, other.minorUnits))
    }

    operator fun times(factor: Long): Money = copy(minorUnits = Math.multiplyExact(minorUnits, factor))

    operator fun unaryMinus(): Money = copy(minorUnits = Math.negateExact(minorUnits))

    override fun compareTo(other: Money): Int {
        requireSameCurrency(other)
        return minorUnits.compareTo(other.minorUnits)
    }

    /**
     * The amount in major units with the currency's own number of decimals, for example 12.50.
     * This is what locale-aware formatting takes as input; it is exact.
     */
    fun toBigDecimal(): BigDecimal = BigDecimal.valueOf(minorUnits, currency.defaultFractionDigits)

    private fun requireSameCurrency(other: Money) {
        require(currency == other.currency) {
            "Cannot combine amounts in ${currency.currencyCode} and ${other.currency.currencyCode}"
        }
    }

    companion object {
        fun zero(currency: Currency): Money = Money(0L, currency)
    }
}
