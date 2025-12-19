package com.phoenix.booklet.screen.settings

data class SettingsUiState(
    val dialogType: SettingsDialogType = SettingsDialogType.None,
    val isLoading: Boolean = false,
    val isDataDeleted: Boolean = false,
)

sealed interface SettingsDialogType {
    data object None: SettingsDialogType
    data object DeleteAll: SettingsDialogType
}