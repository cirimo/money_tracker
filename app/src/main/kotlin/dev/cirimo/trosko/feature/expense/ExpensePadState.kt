package dev.cirimo.trosko.feature.expense

import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.format.DayLabel

/**
 * What the pad for writing an expense shows, whether the expense is new or being corrected.
 *
 * @property selectedCategoryId null while no category is chosen.
 * @property canStepDayForward false when the date is today: an expense cannot be in the future.
 * @property canSave the main button may be pressed.
 */
data class ExpensePadState(
    val amount: AmountInput,
    val categories: List<Category>,
    val selectedCategoryId: CategoryId?,
    val day: DayLabel,
    val canStepDayForward: Boolean,
    val note: String,
    val canSave: Boolean,
) {
    val selectedCategory: Category? get() = categories.firstOrNull { it.id == selectedCategoryId }
}
