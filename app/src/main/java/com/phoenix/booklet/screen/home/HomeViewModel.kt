package com.phoenix.booklet.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.utils.UpdateStateHolder
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
    val bookDao: BookDao,
    val updateStateHolder: UpdateStateHolder,
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _books = MutableStateFlow(emptyList<Book>())
    val books = _books.asStateFlow()
    private val _selectedBooks = MutableStateFlow(emptyList<UUID>())
    val selectedBooks = _selectedBooks.asStateFlow()

    init {
        viewModelScope.launch {
            getAllBooks()
            updateStateHolder.checkForUpdates()
        }
        viewModelScope.launch {
            updateStateHolder.updateState.collect { state ->
                _uiState.update { it.copy(isUpdateAvailable = state.isUpdateAvailable) }
            }
        }
    }

    fun onAction(action: HomeUiActions) {
        when(action) {
            is HomeUiActions.OnSearchQueryChange ->
                _uiState.update { it.copy(searchQuery = action.query) }

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

            is HomeUiActions.DeleteBooks -> {
                removeBooks(action.ids)
                _uiState.update { it.copy(topBarStatus = TopBarStatus.Normal) }
                _selectedBooks.update { emptyList() }
            }

            is HomeUiActions.SelectBook -> {
                // If none exit before operation, initiate select mode
                if (_selectedBooks.value.isEmpty())
                    _uiState.update { it.copy(topBarStatus = TopBarStatus.Select) }

                if (_selectedBooks.value.any { it == action.id }) {
                    _selectedBooks.value -= action.id
                } else {
                    _selectedBooks.value += action.id
                }

                // If non exist after operation, all is deleted, disable selection
                if (_selectedBooks.value.isEmpty())
                    _uiState.update { it.copy(topBarStatus = TopBarStatus.Normal) }
            }
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