package com.redoy.volumecontroll.feature.volume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.domain.repository.UserPreferencesRepository
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VolumeFeatureUiState(
    val volumeState: VolumeState,
    val selectedStream: AudioStream
)

@HiltViewModel
class VolumeViewModel @Inject constructor(
    private val volumeController: VolumeController,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<VolumeFeatureUiState> = preferencesRepository.userPreferences
        .map { prefs ->
            val volState = volumeController.getVolume(prefs.selectedStream)
            VolumeFeatureUiState(
                volumeState = volState,
                selectedStream = prefs.selectedStream
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VolumeFeatureUiState(
                volumeState = VolumeState(0, 10, false, AudioStream.MUSIC),
                selectedStream = AudioStream.MUSIC
            )
        )

    fun updateStream(stream: AudioStream) {
        viewModelScope.launch {
            preferencesRepository.updateSelectedStream(stream)
        }
    }

    fun setVolume(volume: Float) {
        viewModelScope.launch {
            val currentStream = uiState.value.selectedStream
            volumeController.setVolume(currentStream, volume.toInt())
        }
    }

    fun toggleMute() {
        viewModelScope.launch {
            val currentStream = uiState.value.selectedStream
            volumeController.toggleMute(currentStream)
        }
    }
}
