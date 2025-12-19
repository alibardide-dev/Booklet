package com.phoenix.booklet.screen.settings

sealed interface SettingsUiAction {
    data object OnClickBackup: SettingsUiAction
    data object OnClickRestore: SettingsUiAction
    data object OnClickRemoveAll: SettingsUiAction
}