package com.redoy.volumecontroll.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.domain.model.AudioStream

private fun AudioStream.toIcon(): ImageVector {
    return when (this) {
        AudioStream.MUSIC -> Icons.Default.MusicNote
        AudioStream.RING -> Icons.Default.Phone
        AudioStream.ALARM -> Icons.Default.Alarm
        AudioStream.NOTIFICATION -> Icons.Default.Notifications
        AudioStream.SYSTEM -> Icons.Default.Settings
        AudioStream.CALL -> Icons.Default.Call
    }
}

private fun AudioStream.displayName(): String {
    return when (this) {
        AudioStream.MUSIC -> "Media"
        AudioStream.RING -> "Ring"
        AudioStream.ALARM -> "Alarm"
        AudioStream.NOTIFICATION -> "Notif"
        AudioStream.SYSTEM -> "System"
        AudioStream.CALL -> "Call"
    }
}

@Composable
fun VolumeStreamSelector(
    selectedStream: AudioStream,
    onStreamSelected: (AudioStream) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AudioStream.entries.forEach { stream ->
            val isSelected = stream == selectedStream
            FilterChip(
                selected = isSelected,
                onClick = { onStreamSelected(stream) },
                label = { Text(text = stream.displayName()) },
                leadingIcon = {
                    Icon(
                        imageVector = stream.toIcon(),
                        contentDescription = stream.name
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}