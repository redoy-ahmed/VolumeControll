package com.redoy.volumecontroll.data.volume.di

import com.redoy.volumecontroll.core.domain.repository.AppVolumeController
import com.redoy.volumecontroll.core.domain.repository.AudioEffectsController
import com.redoy.volumecontroll.core.domain.repository.VolumeController
import com.redoy.volumecontroll.data.volume.AndroidAppVolumeController
import com.redoy.volumecontroll.data.volume.AndroidAudioEffectsController
import com.redoy.volumecontroll.data.volume.AndroidVolumeController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VolumeDataModule {

    @Binds
    @Singleton
    abstract fun bindVolumeController(
        impl: AndroidVolumeController
    ): VolumeController

    @Binds
    @Singleton
    abstract fun bindAudioEffectsController(
        impl: AndroidAudioEffectsController
    ): AudioEffectsController

    @Binds
    @Singleton
    abstract fun bindAppVolumeController(
        impl: AndroidAppVolumeController
    ): AppVolumeController
}
