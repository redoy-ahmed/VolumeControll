package com.redoy.volumecontroll.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme

@Composable
fun VolumePanel(
    volumeState: VolumeState,
    onVolumeChanged: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onClose: () -> Unit,
    onStreamSelected: (AudioStream) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayVolume by remember(volumeState.currentVolume) {
        androidx.compose.runtime.mutableIntStateOf(
            volumeState.currentVolume
        )
    }
    LaunchedEffect(volumeState.currentVolume) {
        displayVolume = volumeState.currentVolume
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleMute) {
                    Icon(
                        imageVector = if (volumeState.isMuted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                        contentDescription = "Toggle Mute"
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$displayVolume / ${volumeState.maxVolume}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            VolumeSlider(
                value = volumeState.currentVolume.toFloat(),
                valueRange = 0f..volumeState.maxVolume.toFloat().coerceAtLeast(1f),
                onValueChange = {
                    displayVolume = it.toInt()
                    onVolumeChanged(it)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            VolumeStreamSelector(
                selectedStream = volumeState.stream,
                onStreamSelected = onStreamSelected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VolumePanelPreview() {
    VolumeControllTheme {
        VolumePanel(
            volumeState = VolumeState(
                currentVolume = 7,
                maxVolume = 15,
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