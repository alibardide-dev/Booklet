package com.phoenix.booklet.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.screen.home.HomeDialog.Delete
import com.phoenix.booklet.screen.home.HomeDialog.Details
import com.phoenix.booklet.screen.home.HomeDialog.Insert
import com.phoenix.booklet.screen.home.HomeDialog.None
import com.phoenix.booklet.screen.home.HomeDialog.Update
import com.phoenix.booklet.utils.deleteFileFromName
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val bookDao: BookDao
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _books = MutableStateFlow(emptyList<Book>())
    val books = _books.asStateFlow()

    init {
        getAllBooks()
    }

    fun onAction(action: HomeUiActions) {
        when(action) {
            HomeUiActions.DismissDialog ->
                _uiState.update { it.copy(dialogType = None) }

            HomeUiActions.InsertBookDialog ->
                _uiState.update { it.copy(dialogType = Insert) }

            is HomeUiActions.UpdateBookDialog ->
                _uiState.update { it.copy(dialogType = Update(action.id)) }

            is HomeUiActions.DetailsDialog ->
                _uiState.update { it.copy(dialogType = Details(action.id)) }

            is HomeUiActions.DeleteDialog ->
                _uiState.update { it.copy(dialogType = Delete(action.ids)) }

            is HomeUiActions.InsertBook ->
                viewModelScope.launch {
                    bookDao.insertBook(action.book)
                    getAllBooks()
                }

            is HomeUiActions.UpdateBook ->
                viewModelScope.launch {
                    bookDao.updateBook(action.book)
                    getAllBooks()
                }

            is HomeUiActions.ApplyFilter ->
                _uiState.update { it.copy(selectedFilter = action.filter) }

            is HomeUiActions.DeleteBooks ->
                removeBooks(action.ids)
        }
    }

    private fun getAllBooks() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            _books.update { bookDao.getAllBooks() }
        }.invokeOnCompletion {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun removeBooks(ids: List<UUID>) {
        viewModelScope.launch {
            val toDeleteBooks = books.value.filter { book -> ids.any { book.id == it } }
            _books.update { books.value.filter { book -> ids.none { book.id == it } } }

            bookDao.deleteBooks(ids)
            toDeleteBooks.forEach {
                deleteFileFromName(context, it.cover)
            }
        }
    }

}