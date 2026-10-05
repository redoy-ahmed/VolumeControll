package com.redoy.volumecontroll.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.domain.model.AppAudio

@Composable
fun AppVolumePanel(
    apps: List<AppAudio>,
    onVolumeChanged: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Per-App Media Volume",
                style = MaterialTheme.typography.titleMedium
            )
            apps.forEach { app ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "${app.appName} (${app.volume})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = app.volume.toFloat(),
                        valueRange = 0f..app.maxVolume.toFloat().coerceAtLeast(1f),
                        onValueChange = { onVolumeChanged(app.packageName, it) }
                    )
                }
            }
        }
    }
}
