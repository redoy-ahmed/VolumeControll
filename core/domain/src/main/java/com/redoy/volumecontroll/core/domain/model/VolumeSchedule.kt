package com.redoy.volumecontroll.core.domain.model

data class VolumeSchedule(
    val id: String,
    val profileName: String,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean
)