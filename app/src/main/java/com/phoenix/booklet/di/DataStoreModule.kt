package com.phoenix.booklet.di

import android.content.Context
import com.phoenix.booklet.data.DataStoreManager
import com.phoenix.booklet.utils.UpdateStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context) = DataStoreManager(context)

    @Provides
    @Singleton
    fun provideUpdateStateHolder(dataStoreManager: DataStoreManager): UpdateStateHolder =
        UpdateStateHolder(dataStoreManager)


}