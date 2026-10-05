package com.redoy.volumecontroll.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redoy.volumecontroll.core.domain.model.AppVolumeRule
import com.redoy.volumecontroll.core.domain.repository.PerAppVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerAppVolumeViewModel @Inject constructor(
    private val perAppVolumeRepository: PerAppVolumeRepository
) : ViewModel() {

    val rules: StateFlow<List<AppVolumeRule>> = perAppVolumeRepository.volumeRules
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
