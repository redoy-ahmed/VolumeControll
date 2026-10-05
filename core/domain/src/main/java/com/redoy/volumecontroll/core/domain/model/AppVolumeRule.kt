package com.redoy.volumecontroll.core.domain.model

data class AppVolumeRule(
    val packageName: String,
    val appName: String,
    val targetVolume: Int,
    val isEnabled: Boolean
)