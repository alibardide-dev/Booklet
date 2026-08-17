package com.phoenix.booklet.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.booklet.data.DataStoreManager
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.utils.UpdateStateHolder
import com.phoenix.booklet.utils.UpdateStatus
import com.phoenix.booklet.utils.deleteFileFromName
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val bookDao: BookDao,
    val updateStateHolder: UpdateStateHolder,
    val dataStoreManager: DataStoreManager
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _books = MutableStateFlow(emptyList<Book>())
    val books = _books.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isGrid = dataStoreManager.isGridLayout.first()) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getAllBooks()
            updateStateHolder.checkForUpdates()
        }
        viewModelScope.launch {
            updateStateHolder.updateState.collect { state ->
                _uiState.update { it.copy(isUpdateAvailable = state.updateStatus == UpdateStatus.AVAILABLE) }
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
            }

            is HomeUiActions.ToggleFavorite-> {
                toggleFavorite(action.id)
            }

            HomeUiActions.OnToggleGrid -> {
                val isGrid = uiState.value.isGrid
                _uiState.update { it.copy(isGrid = !isGrid) }
                viewModelScope.launch {
                    dataStoreManager.setGridLayout(!isGrid)
                }
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

    private fun toggleFavorite(id: UUID) {
        viewModelScope.launch {
            val isFavorite = _books.value.find { it.id == id }?.isFavorite
            if (isFavorite == null) return@launch

            if (isFavorite)
                bookDao.removeFromFavorite(id)
            else
                bookDao.addToFavorite(id)

            val tempList = books.value.toMutableList()
            val book = tempList.find { it.id == id }!!
            tempList.remove(book)
            tempList.add(book.copy(isFavorite = !isFavorite))

            _books.update { tempList.toList() }
        }
    }

}