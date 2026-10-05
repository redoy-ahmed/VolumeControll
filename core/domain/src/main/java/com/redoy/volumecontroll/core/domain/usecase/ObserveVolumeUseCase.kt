package com.redoy.volumecontroll.core.domain.usecase

import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import kotlinx.coroutines.flow.Flow

class ObserveVolumeUseCase(
    private val volumeController: VolumeController
) {
    operator fun invoke(stream: AudioStream): Flow<VolumeState> {
        return volumeController.observeVolume(stream)
    }
}
