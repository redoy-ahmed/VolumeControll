package com.redoy.volumecontroll.core.domain.usecase

import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.domain.repository.VolumeController

class GetVolumeUseCase(
    private val volumeController: VolumeController
) {
    operator fun invoke(stream: AudioStream): VolumeState {
        return volumeController.getVolume(stream)
    }
}
