package com.redoy.volumecontroll.core.domain.repository

import com.redoy.volumecontroll.core.domain.model.NowPlayingInfo
import kotlinx.coroutines.flow.Flow

interface NowPlayingRepository {
    val nowPlaying: Flow<NowPlayingInfo>
}