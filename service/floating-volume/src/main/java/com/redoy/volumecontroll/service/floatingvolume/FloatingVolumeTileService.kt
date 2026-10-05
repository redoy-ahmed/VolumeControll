package com.redoy.volumecontroll.service.floatingvolume

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class FloatingVolumeTileService : TileService() {

    private var isRunning = false

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.apply {
            state = if (isRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = "Floating Volume"
            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        val serviceIntent = Intent(this, FloatingVolumeService::class.java)
        if (isLocked) {
            unlockAndRun {
                toggleService(serviceIntent)
            }
        } else {
            toggleService(serviceIntent)
        }
    }

    private fun toggleService(intent: Intent) {
        if (isRunning) {
            stopService(intent)
            isRunning = false
            qsTile?.apply {
                state = Tile.STATE_INACTIVE
                updateTile()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isRunning = true
            qsTile?.apply {
                state = Tile.STATE_ACTIVE
                updateTile()
            }
        }
    }
}
