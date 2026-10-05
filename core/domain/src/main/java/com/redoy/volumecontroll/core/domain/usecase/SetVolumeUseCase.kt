package com.redoy.volumecontroll.core.domain.usecase

import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.repository.VolumeController

class SetVolumeUseCase(
    private val volumeController: VolumeController
) {
    operator fun invoke(stream: AudioStream, volume: Int) {
        volumeController.setVolume(stream, volume)
    }
}
