package com.phoenix.booklet.screen.settings

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phoenix.booklet.MainActivity

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
    navigateBack: () -> Unit,
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { settingsViewModel.onAction(SettingsUiActions.CreateBackup(it)) }
    }

    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { settingsViewModel.onAction(SettingsUiActions.RestoreBackup(it)) }
    }

    SettingsScreen(
        onClickBack = { navigateBack() },
        onClickUpdate = { settingsViewModel.onAction(SettingsUiActions.CheckForUpdates) },
        nextUpdateVersion = uiState.nextUpdateVersion,
        updateStatus = uiState.updateStatus,
        backupState = uiState.backupState,
        resetBackupState = { settingsViewModel.onAction(SettingsUiActions.ResetBackupState) },
        isLoading = uiState.isLoading,
        isDataDeleted = uiState.isDataDeleted,
        requestBackup = { createBackupLauncher.launch("booklet_backup_${System.currentTimeMillis()}.zip") },
        requestRestore = { restoreBackupLauncher.launch(arrayOf("application/zip")) },
        requestDeleteData = { settingsViewModel.onAction(SettingsUiActions.RemoveAll) },
        requestRestart = {
            activity?.startActivity(
                Intent(activity, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        },
    )

}