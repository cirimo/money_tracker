package dev.cirimo.trosko.feature.expense

import androidx.lifecycle.SavedStateHandle
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.money.AmountInput
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

private const val KEY_WHOLE = "amount_whole"
private const val KEY_FRACTION = "amount_fraction"
private const val KEY_CATEGORY = "category_id"
private const val KEY_DATE = "picked_date"
private const val KEY_NOTE = "note"

/**
 * What the user has entered so far for one expense.
 *
 * @property pickedDate null means today, whatever day that is when the record is saved, so a
 * form left open over midnight does not quietly file under yesterday.
 */
internal data class ExpenseForm(
    val amount: AmountInput,
    val categoryId: CategoryId? = null,
    val pickedDate: LocalDate? = null,
    val note: String = "",
) {
    /** Writes the form where it survives the process being killed in the background. */
    fun saveTo(savedState: SavedStateHandle) {
        savedState[KEY_WHOLE] = amount.whole
        savedState[KEY_FRACTION] = amount.fraction
        savedState[KEY_CATEGORY] = categoryId?.value?.toString()
        savedState[KEY_DATE] = pickedDate?.toString()
        savedState[KEY_NOTE] = note
    }

    companion object {
        /** Whether a form was ever written to [savedState]. */
        fun isSavedIn(savedState: SavedStateHandle): Boolean = savedState.contains(KEY_NOTE)

        /** The form as it was last saved, or an empty one in [currency]. */
        fun restoredFrom(
            savedState: SavedStateHandle,
            currency: Currency,
        ): ExpenseForm =
            ExpenseForm(
                amount =
                    AmountInput(
                        currency = currency,
                        whole = savedState.get<String>(KEY_WHOLE).orEmpty(),
                        fraction = savedState.get<String>(KEY_FRACTION),
                    ),
                categoryId = savedState.get<String>(KEY_CATEGORY)?.let { CategoryId(UUID.fromString(it)) },
                pickedDate = savedState.get<String>(KEY_DATE)?.let(LocalDate::parse),
                note = savedState.get<String>(KEY_NOTE).orEmpty(),
            )
    }
}
