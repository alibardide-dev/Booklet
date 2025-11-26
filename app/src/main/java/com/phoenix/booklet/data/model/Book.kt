package com.phoenix.booklet.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val author: String,
    val translator: String?,
    val publisher: String?,
    @ColumnInfo("release_year")
    val releaseYear: String?,
    @ColumnInfo("publish_year")
    val publishYear: String?,
    val cover: String?,
    val status: ReadingStatus,
    val dateFinished: Date?,
    @ColumnInfo("created_at")
    val dateCreated: Date,
    @ColumnInfo("updated_at")
    val dateUpdated: Date,
)

enum class ReadingStatus {
    WISHLIST, READING, FINISHED, ARCHIVED
}