package com.phoenix.booklet.screen.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.booklet.data.Result
import com.phoenix.booklet.data.dao.BookDao
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
    val booksDao: BookDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: SettingsUiAction) {
        when (action) {
            SettingsUiAction.OnClickBackup -> {

            }

            SettingsUiAction.OnClickRestore -> {

            }

            SettingsUiAction.OnClickRemoveAll ->
                removeAllData()

            SettingsUiAction.DismissDialog ->
                _uiState.update { it.copy(dialogType = SettingsDialogType.None) }

            SettingsUiAction.OpenRemoveAllDialog ->
                _uiState.update { it.copy(dialogType = SettingsDialogType.DeleteAll) }

        }
    }

    private fun removeAllData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val fileDeleteResult = deleteAllPictures(context)
                if (fileDeleteResult is Result.Success)
                    booksDao.deleteAllBooks()
                Toast.makeText(
                    context,
                    """
                        In case I don't see you, Good Afternoon, Good Evening, And Good Night!
                    """.trimIndent(),
                    Toast.LENGTH_LONG
                ).show()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isDataDeleted = true,
                        dialogType = SettingsDialogType.None
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}