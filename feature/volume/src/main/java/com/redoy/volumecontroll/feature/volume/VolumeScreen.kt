package com.redoy.volumecontroll.feature.volume

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.ui.components.VolumePanel

@Composable
fun VolumeScreen(
    viewModel: VolumeViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    VolumeScreen(
        uiState = uiState,
        onVolumeChanged = { viewModel.setVolume(it) },
        onToggleMute = { viewModel.toggleMute() },
        onStreamSelected = { viewModel.updateStream(it) },
        onClose = onClose,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeScreen(
    uiState: VolumeFeatureUiState,
    onVolumeChanged: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onStreamSelected: (AudioStream) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Volume Control Panel") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VolumePanel(
                volumeState = uiState.volumeState,
                onVolumeChanged = onVolumeChanged,
                onToggleMute = onToggleMute,
                onClose = onClose,
                onStreamSelected = onStreamSelected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VolumeScreenPreview() {
    VolumeControllTheme {
        VolumeScreen(
            uiState = VolumeFeatureUiState(
                volumeState = VolumeState(currentVolume = 8, maxVolume = 15, isMuted = false, stream = AudioStream.MUSIC),
                selectedStream = AudioStream.MUSIC
            ),
            onVolumeChanged = {},
            onToggleMute = {},
            onStreamSelected = {},
            onClose = {}
        )
    }
}