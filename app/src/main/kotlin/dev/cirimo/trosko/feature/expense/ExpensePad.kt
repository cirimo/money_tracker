package dev.cirimo.trosko.feature.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.AmountDisplay
import dev.cirimo.trosko.designsystem.component.CategoryChip
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.component.TroskoTextField
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.money.AmountKey
import dev.cirimo.trosko.format.categoryFill
import dev.cirimo.trosko.format.categoryGlyph
import dev.cirimo.trosko.format.categoryNameText
import dev.cirimo.trosko.format.rememberMoneyFormatter

// Three chips in a row at normal text sizes; two once the text is enlarged enough that three
// names no longer fit side by side.
private const val CHIP_COLUMNS = 3
private const val CHIP_COLUMNS_LARGE_TEXT = 2
private const val LARGE_TEXT_SCALE = 1.3f

/**
 * The pad for writing an expense: amount, date, category, note and keypad, all within reach of
 * a thumb. Recording a new expense and correcting an old one both use it.
 *
 * @param failure what went wrong with the last attempt to write, already in words; null when
 * nothing did.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ExpensePad(
    state: ExpensePadState,
    onKey: (AmountKey) -> Unit,
    onCategoryClick: (CategoryId) -> Unit,
    onDayBack: () -> Unit,
    onDayForward: () -> Unit,
    onNoteChange: (String) -> Unit,
    onNoteFocusChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    failure: String? = null,
) {
    val formatter = rememberMoneyFormatter()
    val amountText = formatter.formatTyping(state.amount)
    val amountDescription = stringResource(R.string.expense_entry_amount_description, amountText)
    val selected = state.selectedCategory
    val chipColumns = if (LocalDensity.current.fontScale > LARGE_TEXT_SCALE) CHIP_COLUMNS_LARGE_TEXT else CHIP_COLUMNS

    Column(
        modifier = modifier.padding(top = TroskoDimens.SpaceM, bottom = TroskoDimens.SpaceL),
        verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
    ) {
        if (failure != null) {
            TroskoText(
                text = failure,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = TroskoTheme.typography.note,
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            AmountDisplay(
                text = amountText,
                modifier = Modifier.semantics { contentDescription = amountDescription },
                fill = selected?.let { categoryFill(it.colour) },
            )
            DateStepper(
                day = state.day,
                canStepForward = state.canStepDayForward,
                onBack = onDayBack,
                onForward = onDayForward,
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth().selectableGroup(),
            // Wide enough that the second outline of the chosen chip clears its neighbours.
            horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
            maxItemsInEachRow = chipColumns,
        ) {
            state.categories.forEach { category ->
                CategoryChip(
                    name = categoryNameText(category.name),
                    glyph = categoryGlyph(category.icon),
                    fill = categoryFill(category.colour),
                    selected = category.id == state.selectedCategoryId,
                    onClick = { onCategoryClick(category.id) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        TroskoTextField(
            value = state.note,
            onValueChange = onNoteChange,
            placeholder = stringResource(R.string.expense_entry_note_placeholder),
            modifier = Modifier.fillMaxWidth().onFocusChanged { onNoteFocusChange(it.isFocused) },
        )
        ExpenseKeypad(
            separator = formatter.decimalSeparator,
            canSave = state.canSave,
            onKey = onKey,
            onSave = onSave,
        )
    }
}

/** Passes a key of a physical keyboard on to the keypad, and says whether it stood for one. */
internal fun typeOnKeypad(
    event: KeyEvent,
    onKey: (AmountKey) -> Unit,
): Boolean {
    val typed = event.utf16CodePoint.toChar()
    val key =
        when {
            event.type != KeyEventType.KeyDown -> null
            event.key == Key.Backspace -> AmountKey.Backspace
            typed in '0'..'9' -> AmountKey.Digit(typed.digitToInt())
            typed == ',' || typed == '.' -> AmountKey.Separator
            else -> null
        }
    key?.let(onKey)
    return key != null
}
