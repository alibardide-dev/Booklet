package com.phoenix.booklet.di

import android.content.Context
import com.phoenix.booklet.utils.UpdateStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UpdateModule {

    @Provides
    @Singleton
    fun provideUpdateStateHolder(@ApplicationContext context: Context): UpdateStateHolder =
        UpdateStateHolder(context)

}