package com.redoy.volumecontroll.core.domain.model

data class AppAudio(
    val packageName: String,
    val appName: String,
    val volume: Int,
    val maxVolume: Int
)