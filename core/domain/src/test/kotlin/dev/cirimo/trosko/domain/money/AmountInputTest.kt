package dev.cirimo.trosko.domain.money

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency

class AmountInputTest {
    private val euro = Currency.getInstance("EUR")
    private val empty = AmountInput(euro)

    private fun AmountInput.digits(digits: String): AmountInput =
        digits.fold(this) { input, char -> input.digit(char.digitToInt()) }

    @Test
    fun `nothing typed is empty and worth zero`() {
        assertTrue(empty.isEmpty)
        assertEquals(Money.zero(euro), empty.toMoney())
    }

    @Test
    fun `digits, a separator and more digits make an amount`() {
        val input = empty.digits("12").separator().digits("50")

        assertEquals(Money(1_250, euro), input.toMoney())
    }

    @Test
    fun `a leading zero is not kept`() {
        val input = empty.digits("007")

        assertEquals("7", input.whole)
    }

    @Test
    fun `the separator first means zero euros`() {
        val input = empty.separator().digits("5")

        assertEquals(Money(50, euro), input.toMoney())
    }

    @Test
    fun `a second separator changes nothing`() {
        val input = empty.digits("3").separator().digits("1")

        assertEquals(input, input.separator())
    }

    @Test
    fun `a third decimal does not fit`() {
        val input = empty.digits("1").separator().digits("99")

        assertEquals(input, input.digit(9))
    }

    @Test
    fun `an eighth whole digit does not fit`() {
        val input = empty.digits("9999999")

        assertEquals(input, input.digit(9))
        assertEquals(Money(999_999_999, euro), input.separator().digits("99").toMoney())
    }

    @Test
    fun `backspace takes back the last key, the separator included`() {
        val typed = empty.digits("12").separator().digits("5")

        val afterOne = typed.backspace()
        val afterTwo = afterOne.backspace()
        val afterThree = afterTwo.backspace()

        assertEquals(AmountInput(euro, whole = "12", fraction = ""), afterOne)
        assertEquals(AmountInput(euro, whole = "12", fraction = null), afterTwo)
        assertEquals(AmountInput(euro, whole = "1", fraction = null), afterThree)
    }

    @Test
    fun `pressing keys is the same as calling them by name`() {
        val pressed =
            empty
                .press(AmountKey.Digit(4))
                .press(AmountKey.Separator)
                .press(AmountKey.Digit(2))
                .press(AmountKey.Digit(0))
                .press(AmountKey.Backspace)

        assertEquals(empty.digits("4").separator().digits("2"), pressed)
    }

    @Test
    fun `backspace on nothing stays nothing`() {
        assertEquals(empty, empty.backspace())
    }

    @Test
    fun `a currency without decimals has no separator`() {
        val yen = AmountInput(Currency.getInstance("JPY")).digits("120")

        assertEquals(yen, yen.separator())
        assertEquals(120L, yen.toMoney().minorUnits)
    }
}
