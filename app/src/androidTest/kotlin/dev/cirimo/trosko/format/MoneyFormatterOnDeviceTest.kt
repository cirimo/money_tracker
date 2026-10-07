package dev.cirimo.trosko.format

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.domain.money.Money
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Currency
import java.util.Locale

/**
 * The JVM tests in `MoneyFormatterTest` run on the desktop's locale data. A phone formats with
 * its own, so the cases that matter most are repeated here on the real thing.
 */
@RunWith(AndroidJUnit4::class)
class MoneyFormatterOnDeviceTest {
    private val euro = Currency.getInstance("EUR")
    private val croatian = MoneyFormatter(Locale.forLanguageTag("hr-HR"))
    private val english = MoneyFormatter(Locale.forLanguageTag("en-US"))

    // Which space and which dash the locale data uses is not what these tests are about.
    private fun String.plain(): String = replace(' ', ' ').replace(' ', ' ').replace('−', '-')

    @Test
    fun finishedAmountsAreWrittenTheWayEachLanguageDoes() {
        assertEquals("12,50 €", croatian.format(Money(1_250, euro)).plain())
        assertEquals("€12.50", english.format(Money(1_250, euro)).plain())
    }

    @Test
    fun zeroAndLargeAmountsKeepEveryDigit() {
        assertEquals("0,00 €", croatian.format(Money(0, euro)).plain())
        assertEquals("9.999.999,99 €", croatian.format(Money(999_999_999, euro)).plain())
        assertEquals("€9,999,999.99", english.format(Money(999_999_999, euro)).plain())
    }

    @Test
    fun expenseCarriesAMinusSign() {
        assertEquals("-12,50 €", croatian.format(Money(-1_250, euro)).plain())
    }

    @Test
    fun amountBeingTypedShowsExactlyWhatWasTyped() {
        assertEquals("0 €", croatian.formatTyping(AmountInput(euro)).plain())
        assertEquals("12, €", croatian.formatTyping(AmountInput(euro, "12", "")).plain())
        assertEquals("12,5 €", croatian.formatTyping(AmountInput(euro, "12", "5")).plain())
        assertEquals("€12.", english.formatTyping(AmountInput(euro, "12", "")).plain())
    }
}
