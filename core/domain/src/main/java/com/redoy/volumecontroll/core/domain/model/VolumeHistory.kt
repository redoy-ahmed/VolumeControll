package com.redoy.volumecontroll.core.domain.model

data class VolumeHistory(
    val timestamp: Long,
    val action: String,
    val details: String
)
