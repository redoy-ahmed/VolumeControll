package com.redoy.volumecontroll

import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeProfile
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import com.redoy.volumecontroll.core.domain.usecase.ApplyProfileUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplyProfileUseCaseTest {

    private class FakeVolumeController : VolumeController {
        var musicVol = 0
        var ringVol = 0
        var alarmVol = 0

        override fun getVolume(stream: AudioStream): VolumeState {
            val vol = when (stream) {
                AudioStream.MUSIC -> musicVol
                AudioStream.RING -> ringVol
                AudioStream.ALARM -> alarmVol
                else -> 0
            }
            return VolumeState(vol, 15, false, stream)
        }

        override fun observeVolume(stream: AudioStream): Flow<VolumeState> = flowOf(getVolume(stream))

        override fun setVolume(stream: AudioStream, volume: Int) {
            when (stream) {
                AudioStream.MUSIC -> musicVol = volume
                AudioStream.RING -> ringVol = volume
                AudioStream.ALARM -> alarmVol = volume
                else -> {}
            }
        }

        override fun increaseVolume(stream: AudioStream) {}
        override fun decreaseVolume(stream: AudioStream) {}
        override fun toggleMute(stream: AudioStream) {}
    }

    @Test
    fun testApplyProfile() {
        val controller = FakeVolumeController()
        val applyProfile = ApplyProfileUseCase(controller)
        val profile = VolumeProfile("silent", "Silent", 0, 0, 2)

        applyProfile(profile)

        assertEquals(0, controller.musicVol)
        assertEquals(0, controller.ringVol)
        assertEquals(2, controller.alarmVol)
    }
}
