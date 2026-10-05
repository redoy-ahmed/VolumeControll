package com.redoy.volumecontroll.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.redoy.volumecontroll.core.domain.model.VolumeProfile
import com.redoy.volumecontroll.core.domain.usecase.ApplyProfileUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class VolumeScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val applyProfileUseCase: ApplyProfileUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val profileName = inputData.getString("profile_name") ?: "Normal"
        val profile = when (profileName.lowercase()) {
            "silent" -> VolumeProfile("silent", "Silent", 0, 0, 5)
            "meeting" -> VolumeProfile("meeting", "Meeting", 0, 0, 0)
            "gaming" -> VolumeProfile("gaming", "Gaming", 15, 5, 5)
            else -> VolumeProfile("normal", "Normal", 10, 7, 10)
        }
        applyProfileUseCase(profile)
        return Result.success()
    }
}
