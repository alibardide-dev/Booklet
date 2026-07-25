package com.phoenix.booklet.screen.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.booklet.data.BackupRepository
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.BackupState
import com.phoenix.booklet.utils.Result
import com.phoenix.booklet.utils.UpdateStateHolder
import com.phoenix.booklet.utils.deleteAllPictures
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val booksDao: BookDao,
    val backupRepository: BackupRepository,
    val updateStateHolder: UpdateStateHolder,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            updateStateHolder.updateState.collect { state ->
                _uiState.update { it.copy(
                    updateStatus = state.updateStatus,
                    nextUpdateVersion = state.nextUpdateVersion
                ) }
            }
        }
    }

    fun onAction(action: SettingsUiActions) {
        when (action) {
            SettingsUiActions.CheckForUpdates -> {
                viewModelScope.launch {
                    updateStateHolder.checkForUpdates()
                }
            }

            is SettingsUiActions.CreateBackup ->
                createBackup(action.uri)

            is SettingsUiActions.RestoreBackup ->
                restoreBackup(action.uri)

            SettingsUiActions.RemoveAll ->
                removeAllData()
        }
    }

    private fun createBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(backupState = BackupState.InProgress(0)) }
            val result = backupRepository.createBackup(uri)
            when(result) {
                is Result.Error ->
                    _uiState.update { it.copy(backupState = BackupState.Error(result.error)) }
                Result.Success ->
                    _uiState.update { it.copy(backupState = BackupState.Success()) }
            }
        }
    }

    private fun restoreBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(backupState = BackupState.InProgress(0)) }
            val result = backupRepository.restoreBackup(uri)
            when(result) {
                is Result.Error ->
                    _uiState.update { it.copy(backupState = BackupState.Error(result.error)) }
                Result.Success ->
                    _uiState.update { it.copy(backupState = BackupState.Success()) }
            }
        }
    }

    private fun removeAllData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val fileDeleteResult = deleteAllPictures(context)
                if (fileDeleteResult is Result.Success)
                    booksDao.deleteAllBooks()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isDataDeleted = true,
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}