package dev.cirimo.trosko.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

private const val DASH_ON_DP = 8f
private const val DASH_OFF_DP = 6f

/**
 * The basic raised shape of the app: a filled shape with an ink outline and a hard shadow
 * offset down and to the right. Buttons, chips, cards and rows are all built on it.
 *
 * @param lift how far the shape stands off the page, from 1 (resting, full shadow) to 0 (pressed
 * flat: no shadow, and the shape has moved to where the shadow was). It is a lambda so that an
 * animated value is read while drawing and never causes recomposition.
 * @param dashed draws the outline dashed, which is how a disabled control looks.
 * @param secondOutline draws another outline just outside the shape; see [SecondOutline].
 */
@Composable
fun StickerSurface(
    fill: Color,
    modifier: Modifier = Modifier,
    shape: Shape = TroskoTheme.shapes.sticker,
    lift: () -> Float = { 1f },
    dashed: Boolean = false,
    secondOutline: SecondOutline? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val ink = TroskoTheme.colors.ink
    val shadow = TroskoTheme.colors.shadow

    Box(
        modifier =
            modifier
                // Room for the shadow, so a pressed shape never leaves its own bounds.
                .padding(end = TroskoDimens.ShadowOffset, bottom = TroskoDimens.ShadowOffset)
                .graphicsLayer {
                    val travel = TroskoDimens.ShadowOffset.toPx() * (1f - lift())
                    translationX = travel
                    translationY = travel
                }.drawWithContent {
                    val outline = shape.createOutline(size, layoutDirection, this)
                    val offset = TroskoDimens.ShadowOffset.toPx() * lift()
                    translate(left = offset, top = offset) { drawOutline(outline, shadow) }
                    drawOutline(outline, fill)
                    drawContent()
                    drawOutline(
                        outline = outline,
                        color = ink,
                        style = Stroke(width = TroskoDimens.Outline.toPx(), pathEffect = dashes(dashed)),
                    )
                    if (secondOutline != null) {
                        drawSecondOutline(shape, ink, dashes(secondOutline == SecondOutline.Dashed))
                    }
                },
        contentAlignment = contentAlignment,
        content = content,
    )
}

private fun ContentDrawScope.dashes(dashed: Boolean): PathEffect? =
    if (dashed) PathEffect.dashPathEffect(floatArrayOf(DASH_ON_DP * density, DASH_OFF_DP * density)) else null

private fun ContentDrawScope.drawSecondOutline(
    shape: Shape,
    ink: Color,
    dashes: PathEffect?,
) {
    val stroke = TroskoDimens.Outline.toPx()
    // From the centre of the first outline to the centre of the second.
    val grow = stroke + TroskoDimens.SecondOutlineGap.toPx()
    val outer = shape.createOutline(Size(size.width + 2 * grow, size.height + 2 * grow), layoutDirection, this)
    translate(left = -grow, top = -grow) {
        drawOutline(outline = outer, color = ink, style = Stroke(width = stroke, pathEffect = dashes))
    }
}

@Preview(showBackground = true)
@Composable
private fun StickerSurfacePreview() {
    TroskoTheme {
        StickerSurface(fill = TroskoTheme.colors.pink, modifier = Modifier.padding(TroskoDimens.SpaceL)) {
            TroskoText(
                text = "12,50 €",
                modifier = Modifier.padding(TroskoDimens.SpaceL),
                style = TroskoTheme.typography.amount,
                color = TroskoTheme.colors.onSticker,
            )
        }
    }
}
