package dev.cirimo.trosko.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoMotion
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * A sticker that can be pressed, the shared behaviour of every button and chip. It goes flat the
 * moment it is touched, stays flat while it is disabled or selected, and shows keyboard focus as
 * a dashed second outline.
 *
 * @param selected null for a plain button. True or false makes it one choice of a group: it is
 * then announced as selected or not, and a selected one lies flat with a solid second outline.
 */
@Composable
internal fun PressableSticker(
    fill: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = TroskoTheme.shapes.sticker,
    enabled: Boolean = true,
    selected: Boolean? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val lift by animateFloatAsState(
        targetValue = if (isPressed || !enabled || selected == true) 0f else 1f,
        animationSpec = if (TroskoTheme.reducedMotion) TroskoMotion.reduced() else TroskoMotion.press(),
        label = "sticker lift",
    )
    // The press is shown by the sticker itself moving, so no ripple is drawn.
    val pressable =
        if (selected == null) {
            Modifier.clickable(
                interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
        } else {
            Modifier.selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
        }

    StickerSurface(
        fill = if (enabled) fill else TroskoTheme.colors.paper,
        modifier =
            modifier
                .then(pressable)
                .defaultMinSize(minWidth = TroskoDimens.MinTouchTarget, minHeight = TroskoDimens.MinTouchTarget),
        shape = shape,
        lift = { lift },
        dashed = !enabled,
        secondOutline =
            when {
                isFocused -> SecondOutline.Dashed
                selected == true -> SecondOutline.Solid
                else -> null
            },
        contentAlignment = contentAlignment,
        content = content,
    )
}
