package com.redoy.volumecontroll.core.domain.repository

import com.redoy.volumecontroll.core.domain.model.AppAudio
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import kotlinx.coroutines.flow.Flow

interface VolumeController {
    fun getVolume(stream: AudioStream): VolumeState
    fun observeVolume(stream: AudioStream): Flow<VolumeState>
    fun setVolume(stream: AudioStream, volume: Int)
    fun increaseVolume(stream: AudioStream)
    fun decreaseVolume(stream: AudioStream)
    fun toggleMute(stream: AudioStream)
    fun getAppAudioSessions(): List<AppAudio> = emptyList()
    fun setAppVolume(packageName: String, volume: Int) {}
}
