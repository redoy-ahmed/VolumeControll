package com.redoy.volumecontroll.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.redoy.volumecontroll.core.domain.model.AppVolumeRule
import com.redoy.volumecontroll.core.domain.repository.PerAppVolumeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

private val Context.perAppDatastore by preferencesDataStore(name = "per_app_volume_preferences")

@Singleton
class PerAppVolumeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PerAppVolumeRepository {

    override val volumeRules: Flow<List<AppVolumeRule>> = flowOf(
        listOf(
            AppVolumeRule("com.spotify.music", "Spotify", 8, true),
            AppVolumeRule("com.google.android.youtube", "YouTube", 5, true)
        )
    )

    override suspend fun saveRule(rule: AppVolumeRule) {
        // Implementation for saving rule
    }

    override suspend fun deleteRule(packageName: String) {
        // Implementation for deleting rule
    }
}