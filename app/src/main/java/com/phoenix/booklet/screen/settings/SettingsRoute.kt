package com.phoenix.booklet.screen.settings

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
    navigateBack: () -> Unit,
    navigateFreshHome: () -> Unit,
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

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
        onClickBackup = { settingsViewModel.onAction(SettingsUiAction.OnClickBackup) },
        onClickRestore = { settingsViewModel.onAction(SettingsUiAction.OnClickRestore) },
        onClickRemoveAll = { openRemoveAllDialog() }
    )

    when(uiState.dialogType) {
        SettingsDialogType.None -> Unit

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
                        onClick = { settingsViewModel.onAction(SettingsUiAction.OnClickRemoveAll) },
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