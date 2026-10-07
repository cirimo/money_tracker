package dev.cirimo.trosko.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

private const val GRID_UNITS = 24f
private const val STROKE_UNITS = 2.2f

/**
 * Draws one icon in ink.
 *
 * @param contentDescription what the icon means, from string resources, when the icon carries
 * meaning by itself; null when it sits next to its word and is decoration.
 */
@Composable
fun InkIcon(
    glyph: InkGlyph,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = TroskoTheme.colors.ink,
) {
    val path = remember(glyph) { PathParser().parsePathString(glyph.pathData).toPath() }
    val described =
        if (contentDescription != null) {
            Modifier.semantics {
                this.contentDescription = contentDescription
                role = Role.Image
            }
        } else {
            Modifier
        }

    Canvas(modifier = modifier.size(TroskoDimens.IconSize).then(described)) {
        scale(scale = size.minDimension / GRID_UNITS, pivot = androidx.compose.ui.geometry.Offset.Zero) {
            drawPath(
                path = path,
                color = tint,
                style = Stroke(width = STROKE_UNITS, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InkIconPreview() {
    TroskoTheme {
        Row(
            modifier = Modifier.padding(TroskoDimens.SpaceL),
            horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
        ) {
            InkGlyph.entries.forEach { InkIcon(glyph = it, contentDescription = null) }
        }
    }
}
