package com.redoy.volumecontroll

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.redoy.volumecontroll.feature.home.HomeScreen
import com.redoy.volumecontroll.feature.home.HomeViewModel
import com.redoy.volumecontroll.feature.settings.AudioEffectsScreen
import com.redoy.volumecontroll.feature.settings.SettingsScreen
import com.redoy.volumecontroll.feature.settings.SettingsViewModel
import com.redoy.volumecontroll.service.floatingvolume.FloatingVolumeService
import com.redoy.volumecontroll.ui.theme.VolumeControllTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var hasOverlayPermission by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hasOverlayPermission = Settings.canDrawOverlays(this)

        setContent {
            VolumeControllTheme {
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
                            onNavigateAudioEffects = { navController.navigate("audio_effects") }
                        )
                    }
                    composable("audio_effects") {
                        val settingsViewModel: SettingsViewModel = hiltViewModel()
                        AudioEffectsScreen(
                            audioEffectsController = settingsViewModel.audioEffectsController,
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
