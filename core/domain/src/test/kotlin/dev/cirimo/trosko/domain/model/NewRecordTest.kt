package dev.cirimo.trosko.domain.model

import dev.cirimo.trosko.domain.money.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.util.Currency

class NewRecordTest {
    private val euro = Currency.getInstance("EUR")
    private val category = BuiltinCategories.all.first().id
    private val today = LocalDate.of(2026, 10, 7)

    private fun expense(
        minorUnits: Long = 1_250,
        occurredOn: LocalDate = today,
        note: String? = null,
    ) = NewRecord.expense(Money(minorUnits, euro), category, occurredOn, note, today)

    private fun NewRecordResult.record(): NewRecord = (this as NewRecordResult.Valid).record

    @Test
    fun `an expense for today is valid and keeps what was given`() {
        val record = expense(note = "bread").record()

        assertEquals(RecordKind.EXPENSE, record.kind)
        assertEquals(Money(1_250, euro), record.amount)
        assertEquals(category, record.categoryId)
        assertEquals(today, record.occurredOn)
        assertEquals("bread", record.note)
    }

    @Test
    fun `one cent is enough`() {
        assertEquals(1L, expense(minorUnits = 1).record().amount.minorUnits)
    }

    @Test
    fun `zero is refused`() {
        assertEquals(NewRecordResult.AmountNotPositive, expense(minorUnits = 0))
    }

    @Test
    fun `a negative amount is refused`() {
        assertEquals(NewRecordResult.AmountNotPositive, expense(minorUnits = -1))
    }

    @Test
    fun `a past date is accepted`() {
        val lastYear = LocalDate.of(2025, 12, 31)

        assertEquals(lastYear, expense(occurredOn = lastYear).record().occurredOn)
    }

    @Test
    fun `tomorrow is refused`() {
        assertEquals(NewRecordResult.DateInFuture, expense(occurredOn = today.plusDays(1)))
    }

    @Test
    fun `a note is trimmed`() {
        assertEquals("coffee", expense(note = "  coffee \n").record().note)
    }

    @Test
    fun `a blank note is no note`() {
        assertNull(expense(note = "   ").record().note)
        assertNull(expense(note = null).record().note)
    }

    @Test
    fun `a note at the limit is kept and one past it is refused`() {
        val longest = "a".repeat(NewRecord.NOTE_MAX_LENGTH)

        assertEquals(longest, expense(note = longest).record().note)
        assertEquals(NewRecordResult.NoteTooLong, expense(note = longest + "a"))
    }
}
