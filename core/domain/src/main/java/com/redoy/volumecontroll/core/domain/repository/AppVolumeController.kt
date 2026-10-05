package com.redoy.volumecontroll.core.domain.repository

import com.redoy.volumecontroll.core.domain.model.AppAudio

interface AppVolumeController {
    fun getActiveAudioApps(): List<AppAudio>
    fun setAppVolume(packageName: String, volume: Int)
}
