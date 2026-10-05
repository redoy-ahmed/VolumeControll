package com.redoy.volumecontroll.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeProfile
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.domain.repository.UserPreferencesRepository
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import com.redoy.volumecontroll.core.domain.usecase.ApplyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val volumeState: VolumeState,
    val isEnabled: Boolean,
    val selectedStream: AudioStream
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val volumeController: VolumeController,
    private val preferencesRepository: UserPreferencesRepository,
    private val applyProfileUseCase: ApplyProfileUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = preferencesRepository.userPreferences
        .map { prefs ->
            val volState = volumeController.getVolume(prefs.selectedStream)
            HomeUiState(
                volumeState = volState,
                isEnabled = prefs.isEnabled,
                selectedStream = prefs.selectedStream
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(
                volumeState = VolumeState(0, 10, false, AudioStream.MUSIC),
                isEnabled = true,
                selectedStream = AudioStream.MUSIC
            )
        )

    fun updateStream(stream: AudioStream) {
        viewModelScope.launch {
            preferencesRepository.updateSelectedStream(stream)
        }
    }

    fun toggleEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateEnabled(enabled)
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

    fun applyProfile(profile: VolumeProfile) {
        viewModelScope.launch {
            applyProfileUseCase(profile)
        }
    }
}