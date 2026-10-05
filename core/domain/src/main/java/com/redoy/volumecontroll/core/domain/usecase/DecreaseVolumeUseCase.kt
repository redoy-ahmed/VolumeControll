package com.redoy.volumecontroll.core.domain.usecase

import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.repository.VolumeController

class DecreaseVolumeUseCase(
    private val volumeController: VolumeController
) {
    operator fun invoke(stream: AudioStream) {
        volumeController.decreaseVolume(stream)
    }
}
