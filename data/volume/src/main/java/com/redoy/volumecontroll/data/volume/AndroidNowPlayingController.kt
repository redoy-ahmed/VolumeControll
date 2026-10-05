package com.redoy.volumecontroll.data.volume

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.redoy.volumecontroll.core.domain.model.NowPlayingInfo
import com.redoy.volumecontroll.core.domain.repository.NowPlayingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNowPlayingController @Inject constructor(
    @ApplicationContext private val context: Context
) : NowPlayingRepository {

    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager

    override val nowPlaying: Flow<NowPlayingInfo> = flow {
        try {
            val controllers = mediaSessionManager?.getActiveSessions(null) ?: emptyList()
            val controller = controllers.firstOrNull()
            val metadata = controller?.metadata
            val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "No Media Playing"
            val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            val playbackState = controller?.playbackState?.state
            val isPlaying = playbackState == PlaybackState.STATE_PLAYING
            emit(NowPlayingInfo(title, artist, isPlaying))
        } catch (e: Exception) {
            emit(NowPlayingInfo("No Media Playing", "Unknown Artist", false))
        }
    }
}