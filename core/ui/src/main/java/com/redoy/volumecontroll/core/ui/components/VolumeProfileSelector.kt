package com.redoy.volumecontroll.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme
import com.redoy.volumecontroll.core.domain.model.VolumeProfile

val predefinedProfiles = listOf(
    VolumeProfile("normal", "Normal", 10, 7, 10),
    VolumeProfile("silent", "Silent", 0, 0, 5),
    VolumeProfile("meeting", "Meeting", 0, 0, 0),
    VolumeProfile("gaming", "Gaming", 15, 5, 5)
)

private fun VolumeProfile.toIcon(): ImageVector {
    return when (this.id) {
        "normal" -> Icons.Default.Speaker
        "silent" -> Icons.Default.NotificationsOff
        "meeting" -> Icons.Default.Vibration
        "gaming" -> Icons.Default.SportsEsports
        else -> Icons.Default.Speaker
    }
}

@Composable
fun VolumeProfileSelector(
    selectedProfileId: String,
    onProfileSelected: (VolumeProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        predefinedProfiles.forEach { profile ->
            val isSelected = profile.id == selectedProfileId
            FilterChip(
                selected = isSelected,
                onClick = { onProfileSelected(profile) },
                label = { Text(text = profile.name) },
                leadingIcon = {
                    Icon(
                        imageVector = profile.toIcon(),
                        contentDescription = profile.name
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VolumeProfileSelectorPreview() {
    VolumeControllTheme {
        VolumeProfileSelector(
            selectedProfileId = "normal",
            onProfileSelected = {}
        )
    }
}