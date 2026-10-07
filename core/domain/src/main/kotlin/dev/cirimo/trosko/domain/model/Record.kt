package dev.cirimo.trosko.domain.model

import dev.cirimo.trosko.domain.money.Money
import java.time.Instant
import java.time.LocalDate

/**
 * One expense or one income, as it was written down.
 *
 * [amount] is always positive; whether the money left or arrived follows from [kind].
 * [occurredOn] is the calendar day the user meant and has no time zone, so it stays the same day
 * wherever the phone travels. [createdAt] is when it was written down, which also orders records
 * within one day.
 */
data class Record(
    val id: RecordId,
    val kind: RecordKind,
    val amount: Money,
    val category: Category,
    val occurredOn: LocalDate,
    val note: String?,
    val createdAt: Instant,
)
