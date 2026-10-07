package dev.cirimo.trosko.feature.expense

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** The place on the back stack that shows [ExpenseEntryRoute]. */
@Serializable
data object ExpenseEntryKey : NavKey
