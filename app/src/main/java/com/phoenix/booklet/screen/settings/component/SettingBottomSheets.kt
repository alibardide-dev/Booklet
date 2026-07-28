package com.phoenix.booklet.screen.settings.component

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.colorResource
import com.phoenix.booklet.R
import com.phoenix.booklet.data.model.BackupState
import com.phoenix.booklet.screen.AlertBottomSheetTemplate
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBackupBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    backupState: BackupState
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        AlertBottomSheetTemplate(
            title = "Create Backup",
            message =
                when (backupState) {
                    is BackupState.Error -> "There was an error creating backup: ${backupState.error}"
                    BackupState.Idle -> "Please choose a folder to save your backup to"
                    is BackupState.InProgress -> "Creating a Backup file"
                    is BackupState.Success -> "Backup created successfully"
                },
            dismissText = "Cancel",
            confirmText =
                when (backupState) {
                    is BackupState.Success -> "OK"
                    is BackupState.Error -> "Exit"
                    BackupState.Idle -> "Choose folder"
                    is BackupState.InProgress -> ""
                },
            isLoading = backupState is BackupState.InProgress,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            onConfirm = {
                scope.launch {
                    if (backupState != BackupState.Idle)
                        sheetState.hide()
                    onConfirm()
                }
            },
            isDismissAllowed = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRestoreBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    backupState: BackupState
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        AlertBottomSheetTemplate(
            title = "Restore Backup",
            message =
                when (backupState) {
                    is BackupState.Error -> "There was an error restoring backup: ${backupState.error}"
                    BackupState.Idle -> "Please choose a Booklet backup file to proceed"
                    is BackupState.InProgress -> "Restoring Backup from file"
                    is BackupState.Success -> "Backup restored successfully"
                },
            dismissText = "Cancel",
            confirmText =
                when (backupState) {
                    is BackupState.Success -> "Restart App"
                    is BackupState.Error -> "Exit"
                    BackupState.Idle -> "Choose File"
                    is BackupState.InProgress -> ""
                },
            isLoading = backupState is BackupState.InProgress,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            onConfirm = {
                scope.launch {
                    if (backupState != BackupState.Idle)
                        sheetState.hide()
                    onConfirm()
                }
            },
            isDismissAllowed = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDeleteAllBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean,
    isDone: Boolean,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        AlertBottomSheetTemplate(
            title = "Remove All Data",
            message =
                if (isDone) "In case I don't see you, Good Afternoon, Good Evening, And Good Night!"
                else "You're about to delete all data in application, including books and their images. ARE YOU SURE?",
            dismissText = "No, Abort",
            confirmText =
                if (isDone) "Restart App"
                else "Yes, Remove All",
            isLoading = isLoading,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            onConfirm = { onConfirm() },
            confirmButtonColors =
                if (!isDone)
                    ButtonDefaults.buttonColors(
                        contentColor = colorResource(R.color.delete_onContainer),
                        containerColor = colorResource(R.color.delete_container)
                    )
                else
                    ButtonDefaults.buttonColors(),
            isDismissAllowed = !isDone
        )
    }
}