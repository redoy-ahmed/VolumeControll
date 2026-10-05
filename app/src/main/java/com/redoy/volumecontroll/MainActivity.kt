package com.redoy.volumecontroll

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.redoy.volumecontroll.core.domain.repository.UserPreferences
import com.redoy.volumecontroll.core.domain.repository.UserPreferencesRepository
import com.redoy.volumecontroll.feature.home.HomeScreen
import com.redoy.volumecontroll.feature.home.HomeViewModel
import com.redoy.volumecontroll.feature.settings.AudioEffectsScreen
import com.redoy.volumecontroll.feature.settings.PerAppVolumeScreen
import com.redoy.volumecontroll.feature.settings.PerAppVolumeViewModel
import com.redoy.volumecontroll.feature.settings.SettingsScreen
import com.redoy.volumecontroll.feature.settings.SettingsViewModel
import com.redoy.volumecontroll.feature.settings.VolumeHistoryScreen
import com.redoy.volumecontroll.feature.settings.VolumeHistoryViewModel
import com.redoy.volumecontroll.service.floatingvolume.FloatingVolumeService
import com.redoy.volumecontroll.ui.theme.VolumeControllTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    private var hasOverlayPermission by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hasOverlayPermission = Settings.canDrawOverlays(this)

        setContent {
            val prefs by preferencesRepository.userPreferences.collectAsState(initial = UserPreferences())
            val darkTheme = when (prefs.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            VolumeControllTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        val homeViewModel: HomeViewModel = hiltViewModel()
                        HomeScreen(
                            viewModel = homeViewModel,
                            hasOverlayPermission = hasOverlayPermission,
                            onRequestPermission = {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName")
                                )
                                startActivity(intent)
                            },
                            onStartService = {
                                if (Settings.canDrawOverlays(this@MainActivity)) {
                                    val serviceIntent = Intent(this@MainActivity, FloatingVolumeService::class.java)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(serviceIntent)
                                    } else {
                                        startService(serviceIntent)
                                    }
                                }
                            },
                            onStopService = {
                                val serviceIntent = Intent(this@MainActivity, FloatingVolumeService::class.java)
                                stopService(serviceIntent)
                            },
                            onNavigateSettings = {
                                navController.navigate("settings")
                            }
                        )
                    }
                    composable("settings") {
                        val settingsViewModel: SettingsViewModel = hiltViewModel()
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateAudioEffects = { navController.navigate("audio_effects") },
                            onNavigatePerAppVolume = { navController.navigate("per_app_volume") },
                            onNavigateVolumeHistory = { navController.navigate("volume_history") }
                        )
                    }
                    composable("audio_effects") {
                        val settingsViewModel: SettingsViewModel = hiltViewModel()
                        AudioEffectsScreen(
                            audioEffectsController = settingsViewModel.audioEffectsController,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("per_app_volume") {
                        val perAppViewModel: PerAppVolumeViewModel = hiltViewModel()
                        PerAppVolumeScreen(
                            viewModel = perAppViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("volume_history") {
                        val historyViewModel: VolumeHistoryViewModel = hiltViewModel()
                        VolumeHistoryScreen(
                            viewModel = historyViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hasOverlayPermission = Settings.canDrawOverlays(this)
    }
}
