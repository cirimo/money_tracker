package dev.cirimo.trosko.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoMotion
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * The main button. It goes down under the finger the moment it is touched: the shadow closes
 * and the button moves into its place. A disabled button has a dashed outline and no shadow, so
 * its state does not depend on a faded colour.
 *
 * Use one filled button per screen for the main action.
 */
@Composable
fun TroskoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val lift by animateFloatAsState(
        targetValue = if (isPressed || !enabled) 0f else 1f,
        animationSpec = if (TroskoTheme.reducedMotion) TroskoMotion.reduced() else TroskoMotion.press(),
        label = "button lift",
    )

    StickerSurface(
        fill = if (enabled) TroskoTheme.colors.yellow else TroskoTheme.colors.paper,
        modifier =
            modifier
                // The press is shown by the button itself moving, so no ripple is drawn.
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ).defaultMinSize(minWidth = TroskoDimens.MinTouchTarget, minHeight = TroskoDimens.ButtonHeight),
        lift = { lift },
        dashed = !enabled,
    ) {
        TroskoText(
            text = text,
            modifier = Modifier.padding(horizontal = TroskoDimens.SpaceXl, vertical = TroskoDimens.SpaceM),
            style = TroskoTheme.typography.label,
            color = if (enabled) TroskoTheme.colors.onSticker else TroskoTheme.colors.inkSoft,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TroskoButtonPreview() {
    TroskoTheme {
        Column(
            modifier = Modifier.padding(TroskoDimens.SpaceL),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
        ) {
            TroskoButton(text = "Zapiši", onClick = {})
            TroskoButton(text = "Zapiši", onClick = {}, enabled = false)
        }
    }
}
