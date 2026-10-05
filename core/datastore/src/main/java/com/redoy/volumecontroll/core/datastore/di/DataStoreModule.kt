package com.redoy.volumecontroll.core.datastore.di

import com.redoy.volumecontroll.core.datastore.PerAppVolumeRepositoryImpl
import com.redoy.volumecontroll.core.datastore.UserPreferencesRepositoryImpl
import com.redoy.volumecontroll.core.domain.repository.PerAppVolumeRepository
import com.redoy.volumecontroll.core.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindPerAppVolumeRepository(
        impl: PerAppVolumeRepositoryImpl
    ): PerAppVolumeRepository
}
