package com.redoy.volumecontroll.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redoy.volumecontroll.core.domain.model.VolumeSchedule
import com.redoy.volumecontroll.core.domain.repository.VolumeScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VolumeScheduleViewModel @Inject constructor(
    private val volumeScheduleRepository: VolumeScheduleRepository
) : ViewModel() {

    val schedules: StateFlow<List<VolumeSchedule>> = volumeScheduleRepository.schedules
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveSchedule(schedule: VolumeSchedule) {
        viewModelScope.launch {
            volumeScheduleRepository.saveSchedule(schedule)
        }
    }

    fun deleteSchedule(id: String) {
        viewModelScope.launch {
            volumeScheduleRepository.deleteSchedule(id)
        }
    }
}
