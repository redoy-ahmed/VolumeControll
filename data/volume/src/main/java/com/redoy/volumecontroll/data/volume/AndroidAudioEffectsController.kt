package com.redoy.volumecontroll.data.volume

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import com.redoy.volumecontroll.core.domain.model.AudioDevice
import com.redoy.volumecontroll.core.domain.repository.AudioEffectsController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAudioEffectsController @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioEffectsController {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    init {
        try {
            equalizer = Equalizer(0, 0)
            bassBoost = BassBoost(0, 0)
            virtualizer = Virtualizer(0, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setEqualizerEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setBassBoost(strength: Short) {
        try {
            bassBoost?.let {
                if (it.enabled) {
                    it.setStrength(strength)
                } else if (strength > 0) {
                    it.enabled = true
                    it.setStrength(strength)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setVirtualizer(strength: Short) {
        try {
            virtualizer?.let {
                if (it.enabled) {
                    it.setStrength(strength)
                } else if (strength > 0) {
                    it.enabled = true
                    it.setStrength(strength)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getConnectedAudioDevices(): List<AudioDevice> {
        val devices = mutableListOf<AudioDevice>()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (device in audioDevices) {
                val typeName = when (device.type) {
                    android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Speaker"
                    android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                    android.media.AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "Bluetooth"
                    android.media.AudioDeviceInfo.TYPE_WIRED_HEADSET,
                    android.media.AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Wired Headset"
                    else -> "Other"
                }
                devices.add(AudioDevice(name = device.productName?.toString() ?: typeName, type = typeName, isConnected = true))
            }
        } else {
            devices.add(AudioDevice(name = "Built-in Speaker", type = "Speaker", isConnected = true))
        }
        return devices
    }
}
