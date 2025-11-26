package com.phoenix.booklet.data

import androidx.room.TypeConverter
import com.phoenix.booklet.data.model.ReadingStatus
import java.util.Date
import java.util.UUID

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? =
        value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? =
        date?.time
}

class StatusConverter {
    @TypeConverter
    fun fromReadingStatus(value: ReadingStatus): String =
        value.name

    @TypeConverter
    fun stringToReadingStatus(name: String): ReadingStatus =
        ReadingStatus.valueOf(name)

}

object UUIDConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID): String =
        uuid.toString()

    @TypeConverter
    fun uuidFromString(string: String): UUID =
        UUID.fromString(string)
}