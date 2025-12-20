package com.phoenix.booklet.screen.settings

import android.net.Uri

sealed interface SettingsUiActions {
    data class CreateBackup(val uri: Uri): SettingsUiActions
    data class RestoreBackup(val uri: Uri): SettingsUiActions
    data object RemoveAll: SettingsUiActions
    data object DismissDialog: SettingsUiActions
    data object OpenRemoveAllDialog: SettingsUiActions
}