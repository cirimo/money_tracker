package dev.cirimo.trosko.feature.expense

import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.RecordLine

/**
 * Everything the entry screen shows.
 *
 * @property selectedCategoryId null while no category is chosen.
 * @property canStepDayForward false when the date is today: an expense cannot be in the future.
 * @property canSave true when there is an amount above zero and a category, and no save is
 * already under way.
 * @property saveFailed the last attempt did not reach storage; what was typed is still here.
 * @property showsSavedNote the short confirmation is shown, from a save until the next key.
 * @property landedId the record written last from this screen; it sits tilted on the page.
 * @property isLandingPending the landing of [landedId] has not been played yet. The screen
 * acknowledges it once the animation starts. It is state, not an event, so it survives a
 * rotation that happens between the save and the first frame.
 */
data class ExpenseEntryUiState(
    val amount: AmountInput,
    val categories: List<Category>,
    val selectedCategoryId: CategoryId?,
    val day: DayLabel,
    val canStepDayForward: Boolean,
    val note: String,
    val canSave: Boolean,
    val saveFailed: Boolean,
    val showsSavedNote: Boolean,
    val latest: List<RecordLine>,
    val landedId: RecordId?,
    val isLandingPending: Boolean,
) {
    val selectedCategory: Category? get() = categories.firstOrNull { it.id == selectedCategoryId }
}
