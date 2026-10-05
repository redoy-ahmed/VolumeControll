package com.redoy.volumecontroll.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme
import com.redoy.volumecontroll.core.domain.model.AudioDevice
import com.redoy.volumecontroll.core.domain.repository.AudioEffectsController
import com.redoy.volumecontroll.core.ui.components.SettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioEffectsScreen(
    audioEffectsController: AudioEffectsController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEqEnabled by remember { mutableStateOf(false) }
    var bassBoostStrength by remember { mutableStateOf(0f) }
    var virtualizerStrength by remember { mutableStateOf(0f) }

    val devices = remember { audioEffectsController.getConnectedAudioDevices() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Equalizer & Sound Effects") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingItem(
                title = "Enable Equalizer",
                checked = isEqEnabled,
                onCheckedChange = {
                    isEqEnabled = it
                    audioEffectsController.setEqualizerEnabled(it)
                }
            )

            Text(
                text = "Bass Boost: ${bassBoostStrength.toInt()}",
                style = MaterialTheme.typography.titleSmall
            )
            Slider(
                value = bassBoostStrength,
                onValueChange = {
                    bassBoostStrength = it
                    audioEffectsController.setBassBoost(it.toInt().toShort())
                },
                valueRange = 0f..1000f
            )

            Text(
                text = "Virtualizer: ${virtualizerStrength.toInt()}",
                style = MaterialTheme.typography.titleSmall
            )
            Slider(
                value = virtualizerStrength,
                onValueChange = {
                    virtualizerStrength = it
                    audioEffectsController.setVirtualizer(it.toInt().toShort())
                },
                valueRange = 0f..1000f
            )

            Text(
                text = "Connected Audio Devices",
                style = MaterialTheme.typography.titleMedium
            )
            devices.forEach { device ->
                Text(text = "• ${device.name} (${device.type})")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Back")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AudioEffectsScreenPreview() {
    val fakeController = object : AudioEffectsController {
        override fun setEqualizerEnabled(enabled: Boolean) {}
        override fun setBassBoost(strength: Short) {}
        override fun setVirtualizer(strength: Short) {}
        override fun getConnectedAudioDevices() = listOf(
            AudioDevice("Built-in Speaker", "Speaker", true),
            AudioDevice("Bluetooth Headphones", "Bluetooth", true)
        )
    }
    VolumeControllTheme {
        AudioEffectsScreen(
            audioEffectsController = fakeController,
            onBack = {}
        )
    }
}
