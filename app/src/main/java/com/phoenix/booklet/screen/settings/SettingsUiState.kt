package com.phoenix.booklet.screen.settings

import com.phoenix.booklet.data.model.BackupState
import com.phoenix.booklet.utils.UpdateStatus

data class SettingsUiState(
    val isLoading: Boolean = false,
    val updateStatus: UpdateStatus = UpdateStatus.IDLE,
    val nextUpdateVersion: String = "",
    val isDataDeleted: Boolean = false,
    val backupState: BackupState = BackupState.Idle
)

sealed interface SettingsDialog {
    data object None: SettingsDialog
    data object Backup: SettingsDialog
    data object Restore: SettingsDialog
    data object DeleteAll: SettingsDialog
}