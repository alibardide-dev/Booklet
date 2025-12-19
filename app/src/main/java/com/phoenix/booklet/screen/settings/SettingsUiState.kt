package com.phoenix.booklet.screen.settings

data class SettingsUiState(
    val dialogType: SettingsDialogType = SettingsDialogType.None,
    val isLoading: Boolean = false,
    val isDataDeleted: Boolean = false,
)

sealed interface SettingsDialogType {
    data object None: SettingsDialogType
    data object CreateBackup: SettingsDialogType
    data object RestoreBackup: SettingsDialogType
    data object CreateSuccessful: SettingsDialogType
    data object RestoreSuccessful: SettingsDialogType
    data object CreateError: SettingsDialogType
    data object RestoreError: SettingsDialogType
    data object DeleteAll: SettingsDialogType
}