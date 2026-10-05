package com.redoy.volumecontroll.core.domain.repository

import com.redoy.volumecontroll.core.domain.model.VolumeHistory
import kotlinx.coroutines.flow.Flow

interface VolumeHistoryRepository {
    val history: Flow<List<VolumeHistory>>
    suspend fun logEvent(action: String, details: String)
}
