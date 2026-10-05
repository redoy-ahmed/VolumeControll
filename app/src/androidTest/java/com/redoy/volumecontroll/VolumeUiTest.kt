package com.redoy.volumecontroll

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.ui.components.VolumePanel
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme
import org.junit.Rule
import org.junit.Test

class VolumeUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testVolumePanelDisplaysCorrectly() {
        composeTestRule.setContent {
            VolumeControllTheme {
                VolumePanel(
                    volumeState = VolumeState(
                        currentVolume = 5,
                        maxVolume = 10,
                        isMuted = false,
                        stream = AudioStream.MUSIC
                    ),
                    onVolumeChanged = {},
                    onToggleMute = {},
                    onClose = {},
                    onStreamSelected = {}
                )
            }
        }

        composeTestRule.onNodeWithText("5 / 10").assertExists()
    }
}