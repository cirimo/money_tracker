package dev.cirimo.trosko.domain.money

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.util.Currency

class MoneyTest {
    private val eur = Currency.getInstance("EUR")
    private val usd = Currency.getInstance("USD")
    private val jpy = Currency.getInstance("JPY")

    @Test
    fun `adding two amounts keeps the currency`() {
        val sum = Money(1_050, eur) + Money(295, eur)

        assertEquals(Money(1_345, eur), sum)
    }

    @Test
    fun `ten cents plus twenty cents is exactly thirty cents`() {
        val sum = Money(10, eur) + Money(20, eur)

        assertEquals(Money(30, eur), sum)
    }

    @Test
    fun `subtracting more than there is gives a negative amount`() {
        val difference = Money(500, eur) - Money(750, eur)

        assertEquals(Money(-250, eur), difference)
        assertTrue(difference.isNegative)
    }

    @Test
    fun `zero is neither positive nor negative`() {
        val zero = Money.zero(eur)

        assertTrue(zero.isZero)
        assertFalse(zero.isPositive)
        assertFalse(zero.isNegative)
    }

    @Test
    fun `multiplying by a whole number is exact`() {
        val total = Money(333, eur) * 3

        assertEquals(Money(999, eur), total)
    }

    @Test
    fun `negating flips the sign`() {
        assertEquals(Money(-1_999, eur), -Money(1_999, eur))
    }

    @Test
    fun `adding amounts in different currencies is refused`() {
        assertThrows(IllegalArgumentException::class.java) { Money(100, eur) + Money(100, usd) }
    }

    @Test
    fun `subtracting amounts in different currencies is refused`() {
        assertThrows(IllegalArgumentException::class.java) { Money(100, eur) - Money(100, usd) }
    }

    @Test
    fun `comparing amounts in different currencies is refused`() {
        assertThrows(IllegalArgumentException::class.java) { Money(100, eur) < Money(200, usd) }
    }

    @Test
    fun `amounts in the same currency compare by value`() {
        assertTrue(Money(199, eur) < Money(200, eur))
        assertTrue(Money(-1, eur) < Money.zero(eur))
    }

    @Test
    fun `large amounts add without losing a cent`() {
        val nineQuadrillion = Money(9_000_000_000_000_000, eur)

        val sum = nineQuadrillion + Money(1, eur)

        assertEquals(Money(9_000_000_000_000_001, eur), sum)
    }

    @Test
    fun `a sum that would overflow throws instead of wrapping around`() {
        assertThrows(ArithmeticException::class.java) { Money(Long.MAX_VALUE, eur) + Money(1, eur) }
    }

    @Test
    fun `a product that would overflow throws instead of wrapping around`() {
        assertThrows(ArithmeticException::class.java) { Money(Long.MAX_VALUE, eur) * 2 }
    }

    @Test
    fun `negating the smallest amount throws instead of wrapping around`() {
        assertThrows(ArithmeticException::class.java) { -Money(Long.MIN_VALUE, eur) }
    }

    @Test
    fun `euro amounts convert to a decimal with two places`() {
        assertEquals(BigDecimal("12.50"), Money(1_250, eur).toBigDecimal())
        assertEquals(BigDecimal("0.05"), Money(5, eur).toBigDecimal())
        assertEquals(BigDecimal("-0.99"), Money(-99, eur).toBigDecimal())
    }

    @Test
    fun `yen amounts convert to a decimal with no places`() {
        assertEquals(BigDecimal("1250"), Money(1_250, jpy).toBigDecimal())
    }
}
