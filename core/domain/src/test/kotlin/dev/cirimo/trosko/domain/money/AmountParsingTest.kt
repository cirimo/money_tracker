package dev.cirimo.trosko.domain.money

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Currency

class AmountParsingTest {
    private val euro = Currency.getInstance("EUR")
    private val yen = Currency.getInstance("JPY")

    private fun euros(minorUnits: Long) = ParsedAmount.Valid(Money(minorUnits, euro))

    @Test
    fun `a comma separates euros from cents`() {
        assertEquals(euros(1_250), parseAmount("12,50", euro))
    }

    @Test
    fun `a dot is accepted as the separator too`() {
        assertEquals(euros(1_250), parseAmount("12.50", euro))
    }

    @Test
    fun `a single decimal digit is tens of cents`() {
        assertEquals(euros(1_250), parseAmount("12,5", euro))
    }

    @Test
    fun `a whole number has no cents`() {
        assertEquals(euros(1_200), parseAmount("12", euro))
    }

    @Test
    fun `a trailing separator is the whole number`() {
        assertEquals(euros(1_200), parseAmount("12,", euro))
    }

    @Test
    fun `a leading separator means zero euros`() {
        assertEquals(euros(5), parseAmount(",05", euro))
    }

    @Test
    fun `one cent is the smallest amount`() {
        assertEquals(euros(1), parseAmount("0,01", euro))
    }

    @Test
    fun `zero is read as zero in every spelling`() {
        assertEquals(euros(0), parseAmount("0", euro))
        assertEquals(euros(0), parseAmount("0,00", euro))
        assertEquals(euros(0), parseAmount("000", euro))
    }

    @Test
    fun `leading zeros and surrounding spaces are ignored`() {
        assertEquals(euros(730), parseAmount("  007,30 ", euro))
    }

    @Test
    fun `the largest amount the keypad allows is exact`() {
        assertEquals(euros(999_999_999), parseAmount("9999999,99", euro))
    }

    @Test
    fun `the largest amount a Long holds is exact`() {
        assertEquals(euros(Long.MAX_VALUE), parseAmount("92233720368547758,07", euro))
    }

    @Test
    fun `one cent more than a Long holds is too large`() {
        assertEquals(ParsedAmount.TooLarge, parseAmount("92233720368547758,08", euro))
    }

    @Test
    fun `an absurdly long number is too large`() {
        assertEquals(ParsedAmount.TooLarge, parseAmount("9".repeat(40), euro))
    }

    @Test
    fun `nothing typed is empty`() {
        assertEquals(ParsedAmount.Empty, parseAmount("", euro))
        assertEquals(ParsedAmount.Empty, parseAmount("   ", euro))
    }

    @Test
    fun `a third decimal is refused instead of rounded`() {
        assertEquals(ParsedAmount.TooManyDecimals, parseAmount("1,234", euro))
        assertEquals(ParsedAmount.TooManyDecimals, parseAmount("0,005", euro))
    }

    @Test
    fun `a sign is not a number`() {
        assertEquals(ParsedAmount.NotANumber, parseAmount("-5", euro))
        assertEquals(ParsedAmount.NotANumber, parseAmount("+5", euro))
    }

    @Test
    fun `letters, two separators and a lone separator are not numbers`() {
        assertEquals(ParsedAmount.NotANumber, parseAmount("12a", euro))
        assertEquals(ParsedAmount.NotANumber, parseAmount("1.234,50", euro))
        assertEquals(ParsedAmount.NotANumber, parseAmount(",", euro))
        assertEquals(ParsedAmount.NotANumber, parseAmount("1e3", euro))
    }

    @Test
    fun `a currency without decimals takes whole numbers only`() {
        assertEquals(ParsedAmount.Valid(Money(1_200, yen)), parseAmount("1200", yen))
        assertEquals(ParsedAmount.TooManyDecimals, parseAmount("12,5", yen))
    }
}
