package com.phoenix.booklet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.phoenix.booklet.data.model.Book

@Dao
interface BookDao {

    @Insert
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookDetails(id: Int): Book

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

}