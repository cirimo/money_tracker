package dev.cirimo.trosko.format

import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.Record
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.money.Money
import java.time.LocalDate

/**
 * A record as a list shows it. Everything a screen would have to work out is already decided
 * here (which day name applies, which way the money went); only the wording is left.
 *
 * @property signedAmount negative for an expense, so the formatter writes the sign.
 */
data class RecordLine(
    val id: RecordId,
    val category: Category,
    val signedAmount: Money,
    val day: DayLabel,
    val note: String?,
) {
    companion object {
        fun of(
            record: Record,
            today: LocalDate,
        ): RecordLine =
            RecordLine(
                id = record.id,
                category = record.category,
                signedAmount =
                    when (record.kind) {
                        RecordKind.EXPENSE -> -record.amount
                        RecordKind.INCOME -> record.amount
                    },
                day = DayLabel.of(record.occurredOn, today),
                note = record.note,
            )
    }
}
