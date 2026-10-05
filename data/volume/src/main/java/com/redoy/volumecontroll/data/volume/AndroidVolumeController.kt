package com.redoy.volumecontroll.data.volume

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.model.VolumeState
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidVolumeController @Inject constructor(
    @ApplicationContext private val context: Context
) : VolumeController {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun AudioStream.toAndroidStream(): Int {
        return when (this) {
            AudioStream.MUSIC -> AudioManager.STREAM_MUSIC
            AudioStream.RING -> AudioManager.STREAM_RING
            AudioStream.ALARM -> AudioManager.STREAM_ALARM
            AudioStream.NOTIFICATION -> AudioManager.STREAM_NOTIFICATION
            AudioStream.SYSTEM -> AudioManager.STREAM_SYSTEM
            AudioStream.CALL -> AudioManager.STREAM_VOICE_CALL
        }
    }

    override fun getVolume(stream: AudioStream): VolumeState {
        val androidStream = stream.toAndroidStream()
        val current = audioManager.getStreamVolume(androidStream)
        val max = audioManager.getStreamMaxVolume(androidStream)
        val isMuted = audioManager.isStreamMute(androidStream)
        return VolumeState(
            currentVolume = current,
            maxVolume = max,
            isMuted = isMuted,
            stream = stream
        )
    }

    override fun observeVolume(stream: AudioStream): Flow<VolumeState> = callbackFlow {
        trySend(getVolume(stream))

        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: android.content.Intent?) {
                if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {
                    val changedStreamType =
                        intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
                    val androidStream = stream.toAndroidStream()
                    if (changedStreamType == -1 || changedStreamType == androidStream) {
                        trySend(getVolume(stream))
                    }
                }
            }
        }

        val filter = android.content.IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        val contentObserver = object :
            android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(getVolume(stream))
            }
        }

        val uri = android.provider.Settings.System.CONTENT_URI
        context.contentResolver.registerContentObserver(uri, true, contentObserver)

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {
            }
            try {
                context.contentResolver.unregisterContentObserver(contentObserver)
            } catch (_: Exception) {
            }
        }
    }.distinctUntilChanged()

    override fun setVolume(stream: AudioStream, volume: Int) {
        val androidStream = stream.toAndroidStream()
        val max = audioManager.getStreamMaxVolume(androidStream)
        val clamped = volume.coerceIn(0, max)
        try {
            audioManager.setStreamVolume(androidStream, clamped, 0)
        } catch (e: SecurityException) {
            Log.e("VolumeController", "SecurityException setting volume", e)
        }
    }

    override fun increaseVolume(stream: AudioStream) {
        val androidStream = stream.toAndroidStream()
        try {
            audioManager.adjustStreamVolume(
                androidStream,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI
            )
        } catch (e: SecurityException) {
            Log.e("VolumeController", "SecurityException increasing volume", e)
        }
    }

    override fun decreaseVolume(stream: AudioStream) {
        val androidStream = stream.toAndroidStream()
        try {
            audioManager.adjustStreamVolume(
                androidStream,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI
            )
        } catch (e: SecurityException) {
            Log.e("VolumeController", "SecurityException decreasing volume", e)
        }
    }

    override fun toggleMute(stream: AudioStream) {
        val androidStream = stream.toAndroidStream()
        try {
            val isMuted = audioManager.isStreamMute(androidStream)
            val direction = if (isMuted) AudioManager.ADJUST_UNMUTE else AudioManager.ADJUST_MUTE
            audioManager.adjustStreamVolume(androidStream, direction, AudioManager.FLAG_SHOW_UI)
        } catch (e: SecurityException) {
            Log.e("VolumeController", "SecurityException toggling mute", e)
        }
    }

    override fun getAppAudioSessions(): List<com.redoy.volumecontroll.core.domain.model.AppAudio> {
        return listOf(
            com.redoy.volumecontroll.core.domain.model.AppAudio(
                "com.spotify.music",
                "Spotify",
                12,
                15
            ),
            com.redoy.volumecontroll.core.domain.model.AppAudio(
                "com.google.android.youtube",
                "YouTube",
                10,
                15
            ),
            com.redoy.volumecontroll.core.domain.model.AppAudio(
                "com.netflix.mediaclient",
                "Netflix",
                8,
                15
            )
        )
    }

    override fun setAppVolume(packageName: String, volume: Int) {
        setVolume(AudioStream.MUSIC, volume)
    }
}