package dev.cirimo.trosko.feature.expense

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * The place on the back stack that shows [ExpenseEditRoute] for one record.
 *
 * @property recordId the record's UUID as text. The screen loads the record itself, so the key
 * stays valid after the process was killed.
 */
@Serializable
data class ExpenseEditKey(
    val recordId: String,
) : NavKey
