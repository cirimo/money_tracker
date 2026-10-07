package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

private const val DASH_ON_DP = 8f
private const val DASH_OFF_DP = 6f

/**
 * One line of text to write on, like a ruled line in the notebook: no box, only an ink line
 * under the words. The line is dashed while the field rests and solid while it has focus.
 *
 * @param placeholder shown in soft ink while the field is empty; it is also the field's name
 * for a screen reader, so it must say what goes here.
 */
@Composable
fun TroskoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val ink = TroskoTheme.colors.ink

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier =
            modifier
                .defaultMinSize(minHeight = TroskoDimens.MinTouchTarget)
                .drawBehind {
                    val stroke = TroskoDimens.Outline.toPx()
                    val y = size.height - stroke / 2
                    val dashes =
                        if (isFocused) {
                            null
                        } else {
                            PathEffect.dashPathEffect(floatArrayOf(DASH_ON_DP * density, DASH_OFF_DP * density))
                        }
                    drawLine(ink, Offset(0f, y), Offset(size.width, y), stroke, StrokeCap.Round, dashes)
                },
        textStyle = TroskoTheme.typography.note.copy(color = ink),
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
        singleLine = true,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(ink),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.padding(horizontal = TroskoDimens.SpaceXs, vertical = TroskoDimens.SpaceS),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    TroskoText(
                        text = placeholder,
                        style = TroskoTheme.typography.note,
                        color = TroskoTheme.colors.inkSoft,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TroskoTextFieldPreview() {
    ThemePreview {
        TroskoTextField(value = "", onValueChange = {}, placeholder = "bilješka")
        TroskoTextField(value = "kruh i mlijeko", onValueChange = {}, placeholder = "bilješka")
    }
}
