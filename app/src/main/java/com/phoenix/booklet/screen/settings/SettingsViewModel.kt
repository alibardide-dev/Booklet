package com.phoenix.booklet.screen.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SettingsViewModel @Inject constructor(

): ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: SettingsUiAction) {
        when(action) {
            SettingsUiAction.OnClickBackup -> {

            }

            SettingsUiAction.OnClickRestore -> {

            }

            SettingsUiAction.OnClickRemoveAll -> {

            }
        }
    }

}