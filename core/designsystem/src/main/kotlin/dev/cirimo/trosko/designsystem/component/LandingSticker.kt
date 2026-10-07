package dev.cirimo.trosko.designsystem.component

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.cirimo.trosko.designsystem.theme.TroskoMotion
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import kotlinx.coroutines.launch

private const val DROP_SCALE = 1.22f
private const val DROP_TILT_FACTOR = 3f
private val DropHeight = 22.dp

/**
 * The signature motion of the app: something that has just been written down lands on the page
 * as a sticker. It drops in slightly too large and too tilted and settles with one bounce. The
 * animation never blocks input; whatever is under it stays usable.
 *
 * Under reduced motion nothing drops, grows or tilts: the content fades in and sits level.
 *
 * @param landing true to play the landing. It plays once each time this turns true.
 * @param tilt the angle, in degrees, the sticker rests at afterwards. A change is followed
 * calmly, so a sticker can be straightened when the next one lands.
 * @param onLand called once the landing has started, so its owner can stop asking for it.
 */
@Composable
fun LandingSticker(
    landing: Boolean,
    tilt: Float,
    modifier: Modifier = Modifier,
    onLand: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val reducedMotion = TroskoTheme.reducedMotion
    // 0 is the top of the drop and 1 is at rest. A sticker that is not landing is simply at rest.
    val progress = remember { Animatable(if (landing) 0f else 1f) }
    val currentOnLand by rememberUpdatedState(onLand)
    val restingTilt by animateFloatAsState(
        targetValue = if (reducedMotion) 0f else tilt,
        animationSpec = if (reducedMotion) TroskoMotion.reduced() else TroskoMotion.settle(),
        label = "resting tilt",
    )

    // The landing runs in the composable's own scope, not in the effect below: telling the owner
    // it has started usually turns `landing` off again, which restarts the effect, and an
    // animation living there would be cancelled at the top of its drop.
    val scope = rememberCoroutineScope()
    LaunchedEffect(landing) {
        if (landing) {
            progress.snapTo(0f)
            scope.launch {
                progress.animateTo(1f, if (reducedMotion) TroskoMotion.reduced() else TroskoMotion.pop())
            }
            currentOnLand()
        }
    }

    Box(
        modifier =
            modifier.graphicsLayer {
                val remaining = 1f - progress.value
                if (reducedMotion) {
                    alpha = progress.value.coerceIn(0f, 1f)
                } else {
                    val scale = 1f + (DROP_SCALE - 1f) * remaining
                    scaleX = scale
                    scaleY = scale
                    translationY = -DropHeight.toPx() * remaining
                    rotationZ = restingTilt * (1f + (DROP_TILT_FACTOR - 1f) * remaining)
                }
            },
    ) {
        content()
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LandingStickerPreview() {
    ThemePreview {
        LandingSticker(landing = false, tilt = 2f) {
            RecordRow("Namirnice", "Danas", "−12,50 €", InkGlyph.Basket, TroskoTheme.colors.green)
        }
    }
}
