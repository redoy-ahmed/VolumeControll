package com.redoy.volumecontroll.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.repository.AudioEffectsController
import com.redoy.volumecontroll.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isEnabled: Boolean = true,
    val selectedStream: AudioStream = AudioStream.MUSIC,
    val buttonSize: Float = 56f,
    val buttonOpacity: Float = 1f,
    val buttonShape: String = "CIRCLE",
    val buttonColorStyle: String = "PRIMARY",
    val themeMode: String = "SYSTEM"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    val audioEffectsController: AudioEffectsController
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = preferencesRepository.userPreferences
        .map { prefs ->
            SettingsUiState(
                isEnabled = prefs.isEnabled,
                selectedStream = prefs.selectedStream,
                buttonSize = prefs.buttonSize.toFloat(),
                buttonOpacity = prefs.buttonOpacity,
                buttonShape = prefs.buttonShape,
                buttonColorStyle = prefs.buttonColorStyle,
                themeMode = prefs.themeMode
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )

    fun updateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateEnabled(enabled)
        }
    }

    fun updateStream(stream: AudioStream) {
        viewModelScope.launch {
            preferencesRepository.updateSelectedStream(stream)
        }
    }

    fun updateButtonSize(size: Float) {
        viewModelScope.launch {
            preferencesRepository.updateButtonSize(size.toInt())
        }
    }

    fun updateButtonOpacity(opacity: Float) {
        viewModelScope.launch {
            preferencesRepository.updateButtonOpacity(opacity)
        }
    }

    fun updateButtonShape(shape: String) {
        viewModelScope.launch {
            preferencesRepository.updateButtonShape(shape)
        }
    }

    fun updateButtonColorStyle(style: String) {
        viewModelScope.launch {
            preferencesRepository.updateButtonColorStyle(style)
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.updateThemeMode(mode)
        }
    }
}
