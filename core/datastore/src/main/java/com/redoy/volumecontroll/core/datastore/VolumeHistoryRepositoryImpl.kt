package com.redoy.volumecontroll.core.datastore

import android.content.Context
import com.redoy.volumecontroll.core.domain.model.VolumeHistory
import com.redoy.volumecontroll.core.domain.repository.VolumeHistoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VolumeHistoryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : VolumeHistoryRepository {

    private val _history = MutableStateFlow<List<VolumeHistory>>(
        listOf(
            VolumeHistory(System.currentTimeMillis() - 60000, "App Started", "Floating volume controller active"),
            VolumeHistory(System.currentTimeMillis() - 30000, "Volume Changed", "Music volume set to 7")
        )
    )

    override val history: Flow<List<VolumeHistory>> = _history.asStateFlow()

    override suspend fun logEvent(action: String, details: String) {
        val current = _history.value.toMutableList()
        current.add(0, VolumeHistory(System.currentTimeMillis(), action, details))
        _history.value = current
    }
}
