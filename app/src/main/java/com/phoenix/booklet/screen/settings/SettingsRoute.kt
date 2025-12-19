package com.phoenix.booklet.screen.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
    navigateBack: () -> Unit,
    navigateFreshHome: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) {  uri ->
        uri?.let { settingsViewModel.onAction(SettingsUiAction.CreateBackup(it)) }
    }

    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {  uri ->
        uri?.let { settingsViewModel.onAction(SettingsUiAction.RestoreBackup(it)) }
    }

    LaunchedEffect(uiState.isDataDeleted) {
        if (uiState.isDataDeleted)
            navigateFreshHome()
    }

    fun closeDialog() {
        settingsViewModel.onAction(SettingsUiAction.DismissDialog)
    }

    fun openRemoveAllDialog() {
        settingsViewModel.onAction(SettingsUiAction.OpenRemoveAllDialog)
    }

    SettingsScreen(
        onClickBack = { navigateBack() },
        onClickBackup = { createBackupLauncher.launch( "booklet_backup_${System.currentTimeMillis()}.zip") },
        onClickRestore = { restoreBackupLauncher.launch(arrayOf("application/zip")) },
        onClickRemoveAll = { openRemoveAllDialog() }
    )

    when(uiState.dialogType) {
        SettingsDialogType.None -> Unit

        SettingsDialogType.CreateBackup ->
            AlertDialog(
            onDismissRequest = {},
            text = { Text("Creating a Backup file") },
            confirmButton = {}
        )

        SettingsDialogType.RestoreBackup ->
            AlertDialog(
                onDismissRequest = {},
                text = { Text("Restoring Backup from file") },
                confirmButton = {}
            )

        SettingsDialogType.CreateError ->
            AlertDialog(
                onDismissRequest = { closeDialog() },
                title = { Text("Create Backup")},
                text = { Text("There was an error creating backup") },
                confirmButton = {
                    Button(onClick = { closeDialog() }) {
                        Text("OK")
                    }
                }
            )

        SettingsDialogType.RestoreError ->
            AlertDialog(
                onDismissRequest = { closeDialog() },
                title = { Text("Restore Backup")},
                text = { Text("There was an error restoring backup") },
                confirmButton = {
                    Button(onClick = { closeDialog() }) {
                        Text("OK")
                    }
                }
            )

        SettingsDialogType.CreateSuccessful ->
            AlertDialog(
                onDismissRequest = { closeDialog() },
                title = { Text("Create Backup")},
                text = { Text("Backup created successfully") },
                confirmButton = {
                    Button(onClick = { closeDialog() }) {
                        Text("OK")
                    }
                }
            )

        SettingsDialogType.RestoreSuccessful ->
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Restore Backup")},
                text = { Text("Backup restored successfully") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                closeDialog()
                                navigateFreshHome()
                            }
                        }
                    ) {
                        Text("OK")
                    }
                }
            )

        SettingsDialogType.DeleteAll ->
            AlertDialog(
                onDismissRequest = { closeDialog() },
                title = { Text("Remove All Data") },
                text = {
                    Text("""
                        You're about to delete all data in application, including books and their images. ARE YOU SURE?
                    """.trimIndent())
                },
                confirmButton = {
                    Button(
                        onClick = { settingsViewModel.onAction(SettingsUiAction.RemoveAll) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        Text("Yes, Remove All")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { closeDialog() },
                        enabled = !uiState.isLoading
                    ) {
                        Text("No, Abort")
                        AnimatedVisibility(uiState.isLoading) {
                            CircularProgressIndicator()
                        }
                    }
                }
            )


    }
}