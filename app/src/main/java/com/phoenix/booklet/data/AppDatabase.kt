package com.phoenix.booklet.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.utils.DatabaseConstants

@Database(
    entities = [Book::class],
    version = DatabaseConstants.DB_VERSION,
    exportSchema = false,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(UUIDConverter::class, DateConverter::class, StatusConverter::class)
abstract class AppDatabase(): RoomDatabase() {
    abstract fun bookDao(): BookDao
}