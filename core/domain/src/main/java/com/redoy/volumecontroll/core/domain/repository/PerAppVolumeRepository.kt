package com.redoy.volumecontroll.core.domain.repository

import com.redoy.volumecontroll.core.domain.model.AppVolumeRule
import kotlinx.coroutines.flow.Flow

interface PerAppVolumeRepository {
    val volumeRules: Flow<List<AppVolumeRule>>
    suspend fun saveRule(rule: AppVolumeRule)
    suspend fun deleteRule(packageName: String)
}