package com.redoy.volumecontroll.core.domain.model

data class VolumeProfile(
    val id: String,
    val name: String,
    val musicVolume: Int,
    val ringVolume: Int,
    val alarmVolume: Int
)
