package dev.cirimo.trosko.feature.expense

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.InkGlyph
import dev.cirimo.trosko.designsystem.component.LandingSticker
import dev.cirimo.trosko.designsystem.component.QuietButton
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.model.BuiltinCategories
import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.model.CategoryName
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.domain.money.AmountKey
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.RecordLine
import dev.cirimo.trosko.format.RecordLineRow
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

// The record on the page sits a little off level, like the amount under it.
private const val PREVIEW_TILT = -1.5f

/**
 * Correcting an expense. The record lies on the page as the list will show it and changes as
 * the pad under it is used; the pad is the one a new expense is written on, filled with what
 * was written down. Deleting sits at the top, away from the thumb, and asks once.
 */
@Composable
fun ExpenseEditScreen(
    uiState: ExpenseEditUiState,
    onKey: (AmountKey) -> Unit,
    onCategoryClick: (CategoryId) -> Unit,
    onDayBack: () -> Unit,
    onDayForward: () -> Unit,
    onDayPick: (LocalDate) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // A physical keyboard types the amount too, except while it is writing the note.
    var isNoteFocused by remember { mutableStateOf(false) }
    // Whether the calendar is open is the screen's own business; nothing about the record
    // changes until a day is chosen. The system's back closes it before it leaves the screen.
    var isCalendarOpen by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = isCalendarOpen) { isCalendarOpen = false }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(TroskoTheme.colors.paper)
                .safeDrawingPadding()
                .onKeyEvent { event ->
                    uiState is ExpenseEditUiState.Editing && !isNoteFocused && typeOnKeypad(event, onKey)
                },
        contentAlignment = Alignment.TopCenter,
    ) {
        val content =
            Modifier
                .widthIn(max = TroskoDimens.MaxContentWidth)
                .padding(horizontal = TroskoDimens.ScreenGutter)
        when (uiState) {
            // While storage has not answered, the page stays blank.
            ExpenseEditUiState.Loading -> {}

            ExpenseEditUiState.Gone -> {
                GonePage(onBack = onBack, modifier = content)
            }

            is ExpenseEditUiState.Editing -> {
                PageAbovePad(
                    page = {
                        RecordPage(
                            uiState = uiState,
                            onDeleteClick = onDeleteClick,
                            onDeleteConfirm = onDeleteConfirm,
                            onDeleteDismiss = onDeleteDismiss,
                        )
                    },
                    pad = {
                        ExpensePad(
                            state = uiState.pad,
                            onKey = onKey,
                            onCategoryClick = onCategoryClick,
                            onDayBack = onDayBack,
                            onDayForward = onDayForward,
                            onNoteChange = onNoteChange,
                            onNoteFocusChange = { isNoteFocused = it },
                            onSave = onSave,
                            failure =
                                if (uiState.saveFailed) {
                                    stringResource(R.string.expense_entry_save_failed_message)
                                } else {
                                    null
                                },
                            onDayClick = { isCalendarOpen = !isCalendarOpen },
                            calendar =
                                if (isCalendarOpen) {
                                    {
                                        DayPicker(
                                            days = uiState.days,
                                            onDayPick = { day ->
                                                onDayPick(day)
                                                isCalendarOpen = false
                                            },
                                        )
                                    }
                                } else {
                                    null
                                },
                        )
                    },
                    modifier = content,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecordPage(
    uiState: ExpenseEditUiState.Editing,
    onDeleteClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = TroskoDimens.SpaceL),
        verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
    ) {
        // The question takes the place of the heading and the button; nothing covers the page.
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            if (uiState.isAskingToDelete) {
                TroskoText(
                    text = stringResource(R.string.expense_edit_delete_question),
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    style = TroskoTheme.typography.label,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS)) {
                    QuietButton(
                        text = stringResource(R.string.expense_edit_delete_button),
                        onClick = onDeleteConfirm,
                        enabled = uiState.canDelete,
                    )
                    QuietButton(
                        text = stringResource(R.string.expense_edit_delete_keep_button),
                        onClick = onDeleteDismiss,
                    )
                }
            } else {
                TroskoText(
                    text = stringResource(R.string.expense_edit_heading),
                    modifier = Modifier.semantics { heading() },
                    style = TroskoTheme.typography.heading,
                )
                QuietButton(
                    text = stringResource(R.string.expense_edit_delete_button),
                    onClick = onDeleteClick,
                    enabled = uiState.canDelete,
                    glyph = InkGlyph.Trash,
                )
            }
        }
        if (uiState.deleteFailed) {
            TroskoText(
                text = stringResource(R.string.expense_edit_delete_failed_message),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = TroskoTheme.typography.note,
            )
        }
        LandingSticker(landing = false, tilt = PREVIEW_TILT) {
            RecordLineRow(line = uiState.preview)
        }
    }
}

@Composable
private fun GonePage(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = TroskoDimens.SpaceL),
        verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
    ) {
        TroskoText(
            text = stringResource(R.string.expense_edit_gone_message),
            style = TroskoTheme.typography.note,
        )
        QuietButton(text = stringResource(R.string.expense_edit_back_button), onClick = onBack)
    }
}

@Preview(showBackground = true, heightDp = 914, widthDp = 412)
@Composable
private fun ExpenseEditScreenPreview() {
    val euro = Currency.getInstance("EUR")
    val categories =
        BuiltinCategories.all.map {
            Category(it.id, it.kind, CategoryName.Builtin(it.key), it.colour, it.icon, it.sortOrder, archivedAt = null)
        }
    TroskoTheme {
        ExpenseEditScreen(
            uiState =
                ExpenseEditUiState.Editing(
                    pad =
                        ExpensePadState(
                            amount = AmountInput(euro, whole = "15", fraction = "20"),
                            categories = categories,
                            selectedCategoryId = categories.first().id,
                            day = DayLabel.Yesterday,
                            canStepDayForward = true,
                            note = "kruh i mlijeko",
                            canSave = true,
                        ),
                    preview =
                        RecordLine(
                            id = RecordId(UUID.randomUUID()),
                            category = categories.first(),
                            signedAmount = Money(-1_520, euro),
                            day = DayLabel.Yesterday,
                            note = "kruh i mlijeko",
                        ),
                    days =
                        DayPickerDays(
                            LocalDate.of(2026, 10, 6),
                            LocalDate.of(2026, 10, 7),
                            LocalDate.of(2026, 10, 7),
                        ),
                    isAskingToDelete = false,
                    canDelete = true,
                    saveFailed = false,
                    deleteFailed = false,
                    finished = null,
                ),
            onKey = {},
            onCategoryClick = {},
            onDayBack = {},
            onDayForward = {},
            onDayPick = {},
            onNoteChange = {},
            onSave = {},
            onDeleteClick = {},
            onDeleteConfirm = {},
            onDeleteDismiss = {},
            onBack = {},
        )
    }
}
