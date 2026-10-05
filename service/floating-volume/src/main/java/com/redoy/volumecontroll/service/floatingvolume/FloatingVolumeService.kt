package com.redoy.volumecontroll.service.floatingvolume

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.redoy.volumecontroll.core.domain.model.AudioStream
import com.redoy.volumecontroll.core.domain.repository.UserPreferencesRepository
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import com.redoy.volumecontroll.core.ui.components.FloatingVolumeButton
import com.redoy.volumecontroll.core.ui.components.VolumePanel
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FloatingVolumeService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    @Inject
    lateinit var volumeController: VolumeController

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var windowManager: WindowManager
    private var floatingView: ComposeView? = null
    private var panelView: ComposeView? = null
    private var floatingParams: WindowManager.LayoutParams? = null

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private var isPanelVisible = false

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForeground(NOTIFICATION_ID, createNotification())
        showFloatingButton()
    }

    private fun createNotification(): Notification {
        val channelId = "floating_volume_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Floating Volume Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Floating Volume Active")
                .setContentText("Tap to open controller app")
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .setContentIntent(pendingIntent)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("Floating Volume Active")
                .setContentText("Tap to open controller app")
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .setContentIntent(pendingIntent)
                .build()
        }
    }

    private fun showFloatingButton() {
        if (floatingView != null) return

        serviceScope.launch {
            val prefs = preferencesRepository.userPreferences.first()

            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            floatingParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = prefs.floatingX.toInt()
                y = prefs.floatingY.toInt()
            }

            val view = ComposeView(this@FloatingVolumeService).apply {
                setViewTreeLifecycleOwner(this@FloatingVolumeService)
                setViewTreeSavedStateRegistryOwner(this@FloatingVolumeService)
                setContent {
                    VolumeControllTheme {
                        FloatingVolumeButton(
                            onClick = {
                                togglePanel()
                            },
                            onDrag = { dragAmount ->
                                floatingParams?.let { params ->
                                    params.x += dragAmount.x.toInt()
                                    params.y += dragAmount.y.toInt()
                                    try {
                                        windowManager.updateViewLayout(floatingView, params)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                            onDragEnd = {
                                floatingParams?.let { params ->
                                    val metrics = android.util.DisplayMetrics()
                                    @Suppress("DEPRECATION")
                                    windowManager.defaultDisplay.getMetrics(metrics)
                                    val screenWidth = metrics.widthPixels

                                    // Snap to left or right edge
                                    val targetX =
                                        if (params.x < screenWidth / 2) 0 else screenWidth - 150
                                    params.x = targetX.coerceIn(0, screenWidth - 100)
                                    params.y = params.y.coerceIn(0, metrics.heightPixels - 100)

                                    try {
                                        windowManager.updateViewLayout(floatingView, params)
                                        serviceScope.launch {
                                            preferencesRepository.updatePosition(
                                                params.x.toFloat(),
                                                params.y.toFloat()
                                            )
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                            size = prefs.buttonSize.dp,
                            opacity = prefs.buttonOpacity
                        )
                    }
                }
            }

            floatingView = view
            try {
                windowManager.addView(view, floatingParams)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun togglePanel() {
        if (isPanelVisible) {
            hidePanel()
        } else {
            showPanel()
        }
    }

    private fun showPanel() {
        if (panelView != null) return

        val panelParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        val view = ComposeView(this@FloatingVolumeService).apply {
            setViewTreeLifecycleOwner(this@FloatingVolumeService)
            setViewTreeSavedStateRegistryOwner(this@FloatingVolumeService)
            setContent {
                VolumeControllTheme {
                    val prefs by preferencesRepository.userPreferences.collectAsState(initial = null)
                    val selectedStream = prefs?.selectedStream ?: AudioStream.MUSIC
                    val volState by volumeController.observeVolume(selectedStream)
                        .collectAsState(initial = volumeController.getVolume(selectedStream))

                    VolumePanel(
                        volumeState = volState,
                        onVolumeChanged = { newValue ->
                            volumeController.setVolume(selectedStream, newValue.toInt())
                        },
                        onToggleMute = {
                            volumeController.toggleMute(selectedStream)
                        },
                        onClose = { hidePanel() },
                        onStreamSelected = { stream ->
                            serviceScope.launch {
                                preferencesRepository.updateSelectedStream(stream)
                            }
                        }
                    )
                }
            }
        }

        panelView = view
        try {
            windowManager.addView(view, panelParams)
            isPanelVisible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hidePanel() {
        panelView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            panelView = null
            isPanelVisible = false
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        hidePanel()
        floatingView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            floatingView = null
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        serviceJob.cancel()
        store.clear()
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}