package dev.cirimo.trosko.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.cirimo.trosko.designsystem.component.LandingSticker
import kotlin.random.Random

// How far off level a record that has just landed may rest, in degrees either way.
private const val MAX_LANDED_TILT = 2.5f

/**
 * A record in a list of the latest, as a sticker that can land: the one just written down or
 * corrected drops onto the page and rests a little tilted until another takes its turn.
 *
 * @param isLanded this is the record that landed last; it sits tilted.
 * @param isLandingPending its landing has not been played yet.
 * @param onLand called once the landing has started.
 */
@Composable
fun RecordLineSticker(
    line: RecordLine,
    isLanded: Boolean,
    isLandingPending: Boolean,
    onLand: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The angle is chosen once per record, a little different every time.
    val tilt = remember { Random.nextFloat() * 2 * MAX_LANDED_TILT - MAX_LANDED_TILT }

    LandingSticker(
        landing = isLanded && isLandingPending,
        tilt = if (isLanded) tilt else 0f,
        modifier = modifier,
        onLand = onLand,
    ) {
        RecordLineRow(line = line, onClick = onClick)
    }
}
