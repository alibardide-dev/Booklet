package com.phoenix.booklet.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.screen.home.HomeDialog.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

}