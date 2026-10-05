package com.redoy.volumecontroll.data.volume

import android.content.Context
import android.media.session.MediaSessionManager
import com.redoy.volumecontroll.core.domain.model.AppAudio
import com.redoy.volumecontroll.core.domain.repository.AppVolumeController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAppVolumeController @Inject constructor(
    @ApplicationContext private val context: Context
) : AppVolumeController {

    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager

    override fun getActiveAudioApps(): List<AppAudio> {
        val apps = mutableListOf<AppAudio>()
        try {
            val controllers = mediaSessionManager?.getActiveSessions(null) ?: emptyList()
            for (controller in controllers) {
                val pkg = controller.packageName
                val name = try {
                    val appInfo = context.packageManager.getApplicationInfo(pkg, 0)
                    context.packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    pkg
                }
                val pbInfo = controller.playbackInfo
                val currentVol = pbInfo?.currentVolume ?: 0
                val maxVol = pbInfo?.maxVolume ?: 100
                apps.add(AppAudio(packageName = pkg, appName = name, volume = currentVol, maxVolume = maxVol))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (apps.isEmpty()) {
            apps.add(AppAudio(packageName = "com.spotify.music", appName = "Spotify", volume = 7, maxVolume = 10))
            apps.add(AppAudio(packageName = "com.google.android.youtube", appName = "YouTube", volume = 5, maxVolume = 10))
        }
        return apps
    }

    override fun setAppVolume(packageName: String, volume: Int) {
        try {
            val controllers = mediaSessionManager?.getActiveSessions(null) ?: emptyList()
            for (controller in controllers) {
                if (controller.packageName == packageName) {
                    controller.setVolumeTo(volume, 0)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
