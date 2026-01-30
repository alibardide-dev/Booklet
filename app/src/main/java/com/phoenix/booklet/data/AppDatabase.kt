package com.phoenix.booklet.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.utils.Constants

@Database(
    entities = [Book::class],
    version = Constants.DB_VERSION,
    exportSchema = true,
)
@TypeConverters(UUIDConverter::class, DateConverter::class, StatusConverter::class)
abstract class AppDatabase(): RoomDatabase() {
    abstract fun bookDao(): BookDao
}