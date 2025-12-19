package com.phoenix.booklet.screen.settings

import android.net.Uri

sealed interface SettingsUiAction {
    data class CreateBackup(val uri: Uri): SettingsUiAction
    data class RestoreBackup(val uri: Uri): SettingsUiAction
    data object RemoveAll: SettingsUiAction
    data object DismissDialog: SettingsUiAction
    data object OpenRemoveAllDialog: SettingsUiAction
}