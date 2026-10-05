package com.redoy.volumecontroll

import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import com.redoy.volumecontroll.core.domain.usecase.GetVolumeUseCase
import com.redoy.volumecontroll.core.domain.usecase.IncreaseVolumeUseCase
import com.redoy.volumecontroll.core.domain.usecase.DecreaseVolumeUseCase
import com.redoy.volumecontroll.core.domain.usecase.SetVolumeUseCase
import com.redoy.volumecontroll.core.domain.usecase.ToggleMuteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

class VolumeUseCasesTest {

    private class FakeVolumeController : VolumeController {
        var currentVol = 5
        var maxVol = 15
        var muted = false

        override fun getVolume(stream: AudioStream): VolumeState {
            return VolumeState(currentVol, maxVol, muted, stream)
        }

        override fun observeVolume(stream: AudioStream): Flow<VolumeState> {
            return flowOf(getVolume(stream))
        }

        override fun setVolume(stream: AudioStream, volume: Int) {
            currentVol = volume.coerceIn(0, maxVol)
        }

        override fun increaseVolume(stream: AudioStream) {
            if (currentVol < maxVol) currentVol++
        }

        override fun decreaseVolume(stream: AudioStream) {
            if (currentVol > 0) currentVol--
        }

        override fun toggleMute(stream: AudioStream) {
            muted = !muted
        }
    }

    @Test
    fun testGetVolume() {
        val controller = FakeVolumeController()
        val getVolume = GetVolumeUseCase(controller)
        val state = getVolume(AudioStream.MUSIC)
        assertEquals(5, state.currentVolume)
        assertEquals(15, state.maxVolume)
        assertEquals(false, state.isMuted)
        assertEquals(AudioStream.MUSIC, state.stream)
    }

    @Test
    fun testIncreaseAndDecreaseVolume() {
        val controller = FakeVolumeController()
        val increase = IncreaseVolumeUseCase(controller)
        val decrease = DecreaseVolumeUseCase(controller)

        increase(AudioStream.MUSIC)
        assertEquals(6, controller.currentVol)

        decrease(AudioStream.MUSIC)
        assertEquals(5, controller.currentVol)
    }

    @Test
    fun testSetVolume() {
        val controller = FakeVolumeController()
        val setVolume = SetVolumeUseCase(controller)

        setVolume(AudioStream.MUSIC, 10)
        assertEquals(10, controller.currentVol)

        // Test boundary clamping
        setVolume(AudioStream.MUSIC, 99)
        assertEquals(15, controller.currentVol)

        setVolume(AudioStream.MUSIC, -5)
        assertEquals(0, controller.currentVol)
    }

    @Test
    fun testToggleMute() {
        val controller = FakeVolumeController()
        val toggleMute = ToggleMuteUseCase(controller)

        assertEquals(false, controller.muted)
        toggleMute(AudioStream.MUSIC)
        assertEquals(true, controller.muted)
        toggleMute(AudioStream.MUSIC)
        assertEquals(false, controller.muted)
    }
}