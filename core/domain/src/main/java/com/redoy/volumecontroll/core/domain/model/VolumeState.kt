package com.redoy.volumecontroll.core.domain.model

data class VolumeState(
    val currentVolume: Int,
    val maxVolume: Int,
    val isMuted: Boolean,
    val stream: AudioStream
)
