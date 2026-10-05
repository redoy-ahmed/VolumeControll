package com.redoy.volumecontroll.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.repository.UserPreferences
import com.redoy.volumecontroll.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val FLOATING_X = floatPreferencesKey("floating_x")
        val FLOATING_Y = floatPreferencesKey("floating_y")
        val SELECTED_STREAM = intPreferencesKey("selected_stream")
        val IS_ENABLED = booleanPreferencesKey("is_enabled")
        val BUTTON_SIZE = intPreferencesKey("button_size")
        val BUTTON_OPACITY = floatPreferencesKey("button_opacity")
    }

    override val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val x = preferences[PreferencesKeys.FLOATING_X] ?: 100f
            val y = preferences[PreferencesKeys.FLOATING_Y] ?: 100f
            val streamOrdinal =
                preferences[PreferencesKeys.SELECTED_STREAM] ?: AudioStream.MUSIC.ordinal
            val stream = AudioStream.entries.getOrNull(streamOrdinal) ?: AudioStream.MUSIC
            val isEnabled = preferences[PreferencesKeys.IS_ENABLED] ?: true
            val buttonSize = preferences[PreferencesKeys.BUTTON_SIZE] ?: 56
            val buttonOpacity = preferences[PreferencesKeys.BUTTON_OPACITY] ?: 0.9f

            UserPreferences(
                floatingX = x,
                floatingY = y,
                selectedStream = stream,
                isEnabled = isEnabled,
                buttonSize = buttonSize,
                buttonOpacity = buttonOpacity
            )
        }

    override suspend fun updatePosition(x: Float, y: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FLOATING_X] = x
            preferences[PreferencesKeys.FLOATING_Y] = y
        }
    }

    override suspend fun updateSelectedStream(stream: AudioStream) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_STREAM] = stream.ordinal
        }
    }

    override suspend fun updateEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ENABLED] = enabled
        }
    }

    override suspend fun updateButtonSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUTTON_SIZE] = size
        }
    }

    override suspend fun updateButtonOpacity(opacity: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUTTON_OPACITY] = opacity
        }
    }
}