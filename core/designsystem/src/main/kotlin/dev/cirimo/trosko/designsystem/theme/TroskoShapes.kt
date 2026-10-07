package dev.cirimo.trosko.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * The shapes. Each has four slightly different corners, which is what makes an outline look
 * drawn by hand instead of generated. See docs/DESIGN.md, shape.
 */
@Immutable
data class TroskoShapes(
    /** Buttons, cards and rows. */
    val sticker: Shape,
    /** Category chips and other small pieces. */
    val chip: Shape,
)

internal val DefaultShapes =
    TroskoShapes(
        sticker = RoundedCornerShape(topStart = 16.dp, topEnd = 20.dp, bottomEnd = 14.dp, bottomStart = 22.dp),
        chip = RoundedCornerShape(topStart = 14.dp, topEnd = 18.dp, bottomEnd = 12.dp, bottomStart = 16.dp),
    )
