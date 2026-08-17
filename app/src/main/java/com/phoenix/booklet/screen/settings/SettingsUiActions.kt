package com.phoenix.booklet.screen.settings

import android.net.Uri

sealed interface SettingsUiActions {
    data object CheckForUpdates: SettingsUiActions
    data class CreateBackup(val uri: Uri): SettingsUiActions
    data class RestoreBackup(val uri: Uri): SettingsUiActions
    data object ResetBackupState: SettingsUiActions
    data object RemoveAll: SettingsUiActions
}