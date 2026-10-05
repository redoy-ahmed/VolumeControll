package com.redoy.volumecontroll.core.domain.repository

import com.redoy.volumecontroll.core.domain.model.VolumeSchedule
import kotlinx.coroutines.flow.Flow

interface VolumeScheduleRepository {
    val schedules: Flow<List<VolumeSchedule>>
    suspend fun saveSchedule(schedule: VolumeSchedule)
    suspend fun deleteSchedule(id: String)
}
