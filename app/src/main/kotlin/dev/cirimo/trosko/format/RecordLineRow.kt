package dev.cirimo.trosko.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.RecordRow

/**
 * A [RecordLine] put into words and drawn as a row. It lives here, not in a feature, because
 * more than one feature lists records and features may not use each other.
 */
@Composable
fun RecordLineRow(
    line: RecordLine,
    modifier: Modifier = Modifier,
) {
    val day = dayLabelText(line.day)
    val note = line.note

    RecordRow(
        title = categoryNameText(line.category.name),
        subtitle = if (note == null) day else stringResource(R.string.record_row_day_and_note, day, note),
        amount = rememberMoneyFormatter().format(line.signedAmount),
        glyph = categoryGlyph(line.category.icon),
        fill = categoryFill(line.category.colour),
        modifier = modifier,
    )
}
