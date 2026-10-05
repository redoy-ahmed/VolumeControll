package com.redoy.volumecontroll.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redoy.volumecontroll.core.domain.model.AppAudio
import com.redoy.volumecontroll.core.domain.model.AppVolumeRule
import com.redoy.volumecontroll.core.domain.repository.AppVolumeController
import com.redoy.volumecontroll.core.domain.repository.PerAppVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerAppVolumeViewModel @Inject constructor(
    private val perAppVolumeRepository: PerAppVolumeRepository,
    private val appVolumeController: AppVolumeController
) : ViewModel() {

    val rules: StateFlow<List<AppVolumeRule>> = perAppVolumeRepository.volumeRules
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activeApps = MutableStateFlow<List<AppAudio>>(emptyList())
    val activeApps: StateFlow<List<AppAudio>> = _activeApps.asStateFlow()

    init {
        loadActiveApps()
    }

    fun loadActiveApps() {
        viewModelScope.launch {
            _activeApps.value = appVolumeController.getActiveAudioApps()
        }
    }

    fun setAppVolume(packageName: String, volume: Int) {
        viewModelScope.launch {
            appVolumeController.setAppVolume(packageName, volume)
            loadActiveApps()
        }
    }

    fun saveRule(rule: AppVolumeRule) {
        viewModelScope.launch {
            perAppVolumeRepository.saveRule(rule)
        }
    }

    fun deleteRule(packageName: String) {
        viewModelScope.launch {
            perAppVolumeRepository.deleteRule(packageName)
        }
    }
}
