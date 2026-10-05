package com.redoy.volumecontroll.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redoy.volumecontroll.core.domain.model.VolumeHistory
import com.redoy.volumecontroll.core.domain.repository.VolumeHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VolumeHistoryViewModel @Inject constructor(
    volumeHistoryRepository: VolumeHistoryRepository
) : ViewModel() {

    val history: StateFlow<List<VolumeHistory>> = volumeHistoryRepository.history
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
