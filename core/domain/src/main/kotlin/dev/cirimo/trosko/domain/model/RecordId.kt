package dev.cirimo.trosko.domain.model

import java.util.UUID

/**
 * Identifies a record for good. It is generated on the device and never reassigned, so it stays
 * the same through export, restore and any future sync.
 */
@JvmInline
value class RecordId(
    val value: UUID,
)
