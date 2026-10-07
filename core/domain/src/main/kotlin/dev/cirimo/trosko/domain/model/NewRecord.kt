package dev.cirimo.trosko.domain.model

import dev.cirimo.trosko.domain.money.Money
import java.time.LocalDate

/**
 * A record that has passed the domain's rules and is ready to be written. It can only be made
 * through [expense], so a repository never has to check one again.
 */
class NewRecord private constructor(
    val kind: RecordKind,
    val amount: Money,
    val categoryId: CategoryId,
    val occurredOn: LocalDate,
    val note: String?,
) {
    companion object {
        /** The longest note that is kept. It is a short remark, not a diary. */
        const val NOTE_MAX_LENGTH = 200

        /**
         * Checks an expense the user wants to write down. [today] is passed in, never read from
         * the system, so the rule about future dates can be tested around midnight.
         *
         * A note is trimmed, and a blank one is stored as no note at all.
         */
        fun expense(
            amount: Money,
            categoryId: CategoryId,
            occurredOn: LocalDate,
            note: String?,
            today: LocalDate,
        ): NewRecordResult {
            val trimmedNote = note?.trim()?.takeIf { it.isNotEmpty() }
            return when {
                !amount.isPositive -> {
                    NewRecordResult.AmountNotPositive
                }

                occurredOn.isAfter(today) -> {
                    NewRecordResult.DateInFuture
                }

                trimmedNote != null && trimmedNote.length > NOTE_MAX_LENGTH -> {
                    NewRecordResult.NoteTooLong
                }

                else -> {
                    NewRecordResult.Valid(
                        NewRecord(RecordKind.EXPENSE, amount, categoryId, occurredOn, trimmedNote),
                    )
                }
            }
        }
    }
}
