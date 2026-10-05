package com.redoy.volumecontroll.core.domain.usecase

import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeProfile
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import javax.inject.Inject

class ApplyProfileUseCase @Inject constructor(
    private val volumeController: VolumeController
) {
    operator fun invoke(profile: VolumeProfile) {
        volumeController.setVolume(AudioStream.MUSIC, profile.musicVolume)
        volumeController.setVolume(AudioStream.RING, profile.ringVolume)
        volumeController.setVolume(AudioStream.ALARM, profile.alarmVolume)
    }
}
