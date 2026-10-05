package com.redoy.volumecontroll.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.ui.components.SettingItem
import com.redoy.volumecontroll.core.ui.components.VolumeStreamSelector

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onNavigateAudioEffects: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        uiState = uiState,
        onEnabledChanged = { viewModel.updateEnabled(it) },
        onStreamSelected = { viewModel.updateStream(it) },
        onButtonSizeChanged = { viewModel.updateButtonSize(it) },
        onButtonOpacityChanged = { viewModel.updateButtonOpacity(it) },
        onButtonShapeSelected = { viewModel.updateButtonShape(it) },
        onButtonColorStyleSelected = { viewModel.updateButtonColorStyle(it) },
        onThemeModeSelected = { viewModel.updateThemeMode(it) },
        onNavigateAudioEffects = onNavigateAudioEffects,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEnabledChanged: (Boolean) -> Unit,
    onStreamSelected: (AudioStream) -> Unit,
    onButtonSizeChanged: (Float) -> Unit,
    onButtonOpacityChanged: (Float) -> Unit,
    onButtonShapeSelected: (String) -> Unit,
    onButtonColorStyleSelected: (String) -> Unit,
    onThemeModeSelected: (String) -> Unit,
    onNavigateAudioEffects: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonSize by remember(uiState.buttonSize) { mutableFloatStateOf(uiState.buttonSize) }
    var buttonOpacity by remember(uiState.buttonOpacity) { mutableFloatStateOf(uiState.buttonOpacity) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") }
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
                title = "Enable Floating Controller",
                checked = uiState.isEnabled,
                onCheckedChange = onEnabledChanged
            )

            Text(
                text = "Default Audio Stream",
                style = MaterialTheme.typography.titleSmall
            )
            VolumeStreamSelector(
                selectedStream = uiState.selectedStream,
                onStreamSelected = onStreamSelected
            )

            Text(
                text = "Theme Mode",
                style = MaterialTheme.typography.titleSmall
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val themes = listOf(
                    "SYSTEM" to "System",
                    "LIGHT" to "Light",
                    "DARK" to "Dark"
                )
                themes.forEach { (key, label) ->
                    FilterChip(
                        selected = uiState.themeMode == key,
                        onClick = { onThemeModeSelected(key) },
                        label = { Text(label) }
                    )
                }
            }

            Text(
                text = "Button Shape",
                style = MaterialTheme.typography.titleSmall
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val shapes = listOf(
                    "CIRCLE" to "Circle",
                    "ROUNDED_SQUARE" to "Rounded Square",
                    "PILL" to "Pill"
                )
                shapes.forEach { (key, label) ->
                    FilterChip(
                        selected = uiState.buttonShape == key,
                        onClick = { onButtonShapeSelected(key) },
                        label = { Text(label) }
                    )
                }
            }

            Text(
                text = "Button Color Style",
                style = MaterialTheme.typography.titleSmall
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val styles = listOf(
                    "PRIMARY" to "Primary",
                    "SECONDARY" to "Secondary",
                    "TERTIARY" to "Tertiary"
                )
                styles.forEach { (key, label) ->
                    FilterChip(
                        selected = uiState.buttonColorStyle == key,
                        onClick = { onButtonColorStyleSelected(key) },
                        label = { Text(label) }
                    )
                }
            }

            Text(
                text = "Button Size: ${buttonSize.toInt()} dp",
                style = MaterialTheme.typography.titleSmall
            )
            Slider(
                value = buttonSize,
                onValueChange = {
                    buttonSize = it
                    onButtonSizeChanged(it)
                },
                valueRange = 40f..80f
            )

            Text(
                text = "Button Opacity: ${(buttonOpacity * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall
            )
            Slider(
                value = buttonOpacity,
                onValueChange = {
                    buttonOpacity = it
                    onButtonOpacityChanged(it)
                },
                valueRange = 0.2f..1f
            )

            Button(
                onClick = onNavigateAudioEffects,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Equalizer & Sound Effects")
            }

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
fun SettingsScreenPreview() {
    VolumeControllTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                isEnabled = true,
                selectedStream = AudioStream.MUSIC,
                buttonSize = 56f,
                buttonOpacity = 0.9f,
                buttonShape = "CIRCLE",
                buttonColorStyle = "PRIMARY",
                themeMode = "SYSTEM"
            ),
            onEnabledChanged = {},
            onStreamSelected = {},
            onButtonSizeChanged = {},
            onButtonOpacityChanged = {},
            onButtonShapeSelected = {},
            onButtonColorStyleSelected = {},
            onThemeModeSelected = {},
            onNavigateAudioEffects = {},
            onBack = {}
        )
    }
}
