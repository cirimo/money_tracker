package dev.cirimo.trosko.feature.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.InkGlyph
import dev.cirimo.trosko.designsystem.component.QuietButton
import dev.cirimo.trosko.designsystem.component.QuietIconButton
import dev.cirimo.trosko.designsystem.component.TroskoButton
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.money.AmountKey

private val DigitRows = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
private const val DIGIT_COLUMNS = 3f
private const val WIDE_KEY_COLUMNS = 2f

/**
 * The app's own keypad for the amount: digits and the separator on the left, and on the right
 * the backspace with the main button under it, as tall as three keys, where the thumb rests.
 *
 * @param separator the decimal separator as the current locale writes it.
 */
@Composable
fun ExpenseKeypad(
    separator: String,
    canSave: Boolean,
    onKey: (AmountKey) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keySpacing = Arrangement.spacedBy(TroskoDimens.SpaceS)
    val key = Modifier.heightIn(min = TroskoDimens.KeyHeight)
    val separatorDescription = stringResource(R.string.expense_entry_separator_description)

    // The row is as tall as the digits need, and the right column stretches to match.
    Row(modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = keySpacing) {
        Column(modifier = Modifier.weight(DIGIT_COLUMNS), verticalArrangement = keySpacing) {
            DigitRows.forEach { digits ->
                Row(horizontalArrangement = keySpacing) {
                    digits.forEach { digit -> DigitKey(digit, onKey, key.weight(1f)) }
                }
            }
            Row(horizontalArrangement = keySpacing) {
                QuietButton(
                    text = separator,
                    onClick = { onKey(AmountKey.Separator) },
                    modifier = key.weight(1f).semantics { contentDescription = separatorDescription },
                    style = TroskoTheme.typography.title,
                )
                DigitKey(0, onKey, key.weight(WIDE_KEY_COLUMNS))
            }
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = keySpacing) {
            QuietIconButton(
                glyph = InkGlyph.Backspace,
                contentDescription = stringResource(R.string.expense_entry_backspace_description),
                onClick = { onKey(AmountKey.Backspace) },
                modifier = key.fillMaxWidth(),
            )
            TroskoButton(
                text = stringResource(R.string.expense_entry_save_button),
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().weight(1f),
                enabled = canSave,
            )
        }
    }
}

@Composable
private fun DigitKey(
    digit: Int,
    onKey: (AmountKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuietButton(
        text = digit.toString(),
        onClick = { onKey(AmountKey.Digit(digit)) },
        modifier = modifier,
        style = TroskoTheme.typography.title,
    )
}

@Preview(showBackground = true)
@Composable
private fun ExpenseKeypadPreview() {
    TroskoTheme {
        ExpenseKeypad(
            separator = ",",
            canSave = true,
            onKey = {},
            onSave = {},
        )
    }
}
