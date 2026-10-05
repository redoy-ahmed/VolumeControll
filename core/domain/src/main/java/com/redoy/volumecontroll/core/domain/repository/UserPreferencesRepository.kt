package com.redoy.volumecontroll.core.domain.repository

import com.redoy.volumecontroll.core.domain.model.AudioStream
import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val floatingX: Float = 100f,
    val floatingY: Float = 100f,
    val selectedStream: AudioStream = AudioStream.MUSIC,
    val isEnabled: Boolean = true,
    val buttonSize: Int = 56,
    val buttonOpacity: Float = 0.9f
)

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updatePosition(x: Float, y: Float)
    suspend fun updateSelectedStream(stream: AudioStream)
    suspend fun updateEnabled(enabled: Boolean)
    suspend fun updateButtonSize(size: Int)
    suspend fun updateButtonOpacity(opacity: Float)
}
