package dev.cirimo.trosko.format

import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.domain.money.Money
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Currency
import java.util.Locale

class MoneyFormatterTest {
    private val euro = Currency.getInstance("EUR")
    private val croatian = MoneyFormatter(Locale.forLanguageTag("hr-HR"))
    private val english = MoneyFormatter(Locale.forLanguageTag("en-US"))

    private fun euros(minorUnits: Long) = Money(minorUnits, euro)

    private fun typed(
        whole: String,
        fraction: String? = null,
    ) = AmountInput(euro, whole, fraction)

    // Which space separates the number from the symbol, and which dash is the minus, differ
    // between versions of the locale data. Neither is what these tests are about.
    private fun String.plain(): String = replace(' ', ' ').replace(' ', ' ').replace('−', '-')

    @Test
    fun `croatian puts a comma before the cents and the symbol after`() {
        assertEquals("12,50 €", croatian.format(euros(1_250)).plain())
    }

    @Test
    fun `english puts a dot before the cents and the symbol first`() {
        assertEquals("€12.50", english.format(euros(1_250)).plain())
    }

    @Test
    fun `zero keeps both decimals`() {
        assertEquals("0,00 €", croatian.format(euros(0)).plain())
        assertEquals("€0.00", english.format(euros(0)).plain())
    }

    @Test
    fun `one cent is not rounded away`() {
        assertEquals("0,01 €", croatian.format(euros(1)).plain())
        assertEquals("€0.01", english.format(euros(1)).plain())
    }

    @Test
    fun `large amounts are grouped the way each language does it`() {
        assertEquals("9.999.999,99 €", croatian.format(euros(999_999_999)).plain())
        assertEquals("€9,999,999.99", english.format(euros(999_999_999)).plain())
    }

    @Test
    fun `the largest amount there is keeps every digit`() {
        assertEquals("92.233.720.368.547.758,07 €", croatian.format(euros(Long.MAX_VALUE)).plain())
        assertEquals("€92,233,720,368,547,758.07", english.format(euros(Long.MAX_VALUE)).plain())
    }

    @Test
    fun `a negative amount carries its sign`() {
        assertEquals("-12,50 €", croatian.format(euros(-1_250)).plain())
        assertEquals("-€12.50", english.format(euros(-1_250)).plain())
    }

    @Test
    fun `the separator key shows what the language writes`() {
        assertEquals(",", croatian.decimalSeparator)
        assertEquals(".", english.decimalSeparator)
    }

    @Test
    fun `nothing typed shows a bare zero`() {
        assertEquals("0 €", croatian.formatTyping(typed("")).plain())
        assertEquals("€0", english.formatTyping(typed("")).plain())
    }

    @Test
    fun `whole digits show without decimals`() {
        assertEquals("12 €", croatian.formatTyping(typed("12")).plain())
        assertEquals("€12", english.formatTyping(typed("12")).plain())
    }

    @Test
    fun `the separator shows as soon as it is pressed`() {
        assertEquals("12, €", croatian.formatTyping(typed("12", "")).plain())
        assertEquals("€12.", english.formatTyping(typed("12", "")).plain())
    }

    @Test
    fun `only the decimals typed so far show, zeros included`() {
        assertEquals("12,5 €", croatian.formatTyping(typed("12", "5")).plain())
        assertEquals("12,50 €", croatian.formatTyping(typed("12", "50")).plain())
        assertEquals("0,0 €", croatian.formatTyping(typed("", "0")).plain())
        assertEquals("€12.05", english.formatTyping(typed("12", "05")).plain())
    }

    @Test
    fun `the largest amount the keypad allows is grouped while typing`() {
        assertEquals("9.999.999,99 €", croatian.formatTyping(typed("9999999", "99")).plain())
    }
}
