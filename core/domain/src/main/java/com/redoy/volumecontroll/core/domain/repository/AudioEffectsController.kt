package com.redoy.volumecontroll.core.domain.repository

import com.redoy.volumecontroll.core.domain.model.AudioDevice

interface AudioEffectsController {
    fun setEqualizerEnabled(enabled: Boolean)
    fun setBassBoost(strength: Short)
    fun setVirtualizer(strength: Short)
    fun getConnectedAudioDevices(): List<AudioDevice>
}
