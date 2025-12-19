package com.phoenix.booklet.utils

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.phoenix.booklet.data.AppDatabase
import com.phoenix.booklet.data.BackupRepository
import com.phoenix.booklet.data.dao.BookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = DatabaseConstants.DB_NAME
        ).build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().create()

    @Provides
    @Singleton
    fun provideBookDao(database: AppDatabase): BookDao =
        database.bookDao()

    @Provides
    @Singleton
    fun provideBackupRepository(
        database: AppDatabase,
        @ApplicationContext context: Context,
        gson: Gson
    ): BackupRepository =
        BackupRepository(database, context, gson)

}