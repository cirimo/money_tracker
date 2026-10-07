package dev.cirimo.trosko.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val STICKER = "sticker"

@RunWith(AndroidJUnit4::class)
class LandingStickerTest {
    @get:Rule
    val composeRule = createComposeRule()

    /**
     * The owner of a landing stops asking for it as soon as it is told the landing has started.
     * That must not stop the landing itself: a sticker frozen at the top of its drop is too
     * large and sits above its place.
     */
    @Test
    fun stickerComesToRestAfterItsOwnerStopsAskingForTheLanding() {
        var landing by mutableStateOf(true)
        composeRule.setContent {
            TroskoTheme {
                LandingSticker(landing = landing, tilt = 0f, onLand = { landing = false }) {
                    Box(Modifier.size(100.dp).testTag(STICKER))
                }
            }
        }

        composeRule.waitForIdle()

        // At rest and level, the content sits exactly where layout put it.
        val position = composeRule.onNodeWithTag(STICKER).fetchSemanticsNode().positionInRoot
        assertEquals(Offset.Zero, position)
    }

    @Test
    fun stickerThatIsNotLandingIsAtRestFromTheStart() {
        composeRule.setContent {
            TroskoTheme {
                LandingSticker(landing = false, tilt = 0f) {
                    Box(Modifier.size(100.dp).testTag(STICKER))
                }
            }
        }

        val position = composeRule.onNodeWithTag(STICKER).fetchSemanticsNode().positionInRoot
        assertEquals(Offset.Zero, position)
    }
}
