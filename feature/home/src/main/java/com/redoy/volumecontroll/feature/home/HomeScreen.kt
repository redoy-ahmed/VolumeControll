package com.redoy.volumecontroll.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeProfile
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.ui.components.PermissionCard
import com.redoy.volumecontroll.core.ui.components.StatusIndicator
import com.redoy.volumecontroll.core.ui.components.VolumePanel
import com.redoy.volumecontroll.core.ui.components.VolumeProfileSelector
import com.redoy.volumecontroll.core.ui.components.VolumeStreamSelector
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    hasOverlayPermission: Boolean,
    onRequestPermission: () -> Unit,
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    onNavigateSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        hasOverlayPermission = hasOverlayPermission,
        onRequestPermission = onRequestPermission,
        onStartService = onStartService,
        onStopService = onStopService,
        onNavigateSettings = onNavigateSettings,
        onVolumeChanged = { viewModel.setVolume(it) },
        onToggleMute = { viewModel.toggleMute() },
        onStreamSelected = { viewModel.updateStream(it) },
        onProfileSelected = { viewModel.applyProfile(it) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    hasOverlayPermission: Boolean,
    onRequestPermission: () -> Unit,
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    onNavigateSettings: () -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onStreamSelected: (AudioStream) -> Unit,
    onProfileSelected: (VolumeProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Floating Volume Controller") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusIndicator(isActive = uiState.isEnabled && hasOverlayPermission)

            VolumePanel(
                volumeState = uiState.volumeState,
                onVolumeChanged = onVolumeChanged,
                onToggleMute = onToggleMute,
                onClose = {},
                onStreamSelected = onStreamSelected
            )

            Text(
                text = "Quick Volume Profiles",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            VolumeProfileSelector(
                selectedProfileId = "",
                onProfileSelected = onProfileSelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!hasOverlayPermission) {
                Button(onClick = onRequestPermission, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Grant Overlay Permission")
                }
            } else {
                Button(
                    onClick = {
                        if (uiState.isEnabled) {
                            onStartService()
                        } else {
                            onStopService()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (uiState.isEnabled) "Start Floating Service" else "Stop Service")
                }
            }

            Button(onClick = onNavigateSettings, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Settings")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    VolumeControllTheme {
        HomeScreen(
            uiState = HomeUiState(
                volumeState = VolumeState(currentVolume = 7, maxVolume = 15, isMuted = false, stream = AudioStream.MUSIC),
                isEnabled = true,
                selectedStream = AudioStream.MUSIC
            ),
            hasOverlayPermission = true,
            onRequestPermission = {},
            onStartService = {},
            onStopService = {},
            onNavigateSettings = {},
            onVolumeChanged = {},
            onToggleMute = {},
            onStreamSelected = {},
            onProfileSelected = {}
        )
    }
}