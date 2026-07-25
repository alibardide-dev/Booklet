package com.phoenix.booklet.screen.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phoenix.booklet.BuildConfig
import com.phoenix.booklet.R
import com.phoenix.booklet.data.model.BackupState
import com.phoenix.booklet.screen.settings.component.SettingsBackupBottomSheet
import com.phoenix.booklet.screen.settings.component.SettingsDeleteAllBottomSheet
import com.phoenix.booklet.screen.settings.component.SettingsRestoreBottomSheet
import com.phoenix.booklet.utils.UpdateStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onClickBack: () -> Unit,
    onClickUpdate: () -> Unit,
    updateStatus: UpdateStatus,
    nextUpdateVersion: String,
    backupState: BackupState,
    isLoading: Boolean,
    isDataDeleted: Boolean,
    requestBackup: () -> Unit,
    requestRestore: () -> Unit,
    requestDeleteData: () -> Unit,
    requestRestart: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    var dialogType: SettingsDialog by remember { mutableStateOf(SettingsDialog.None) }

    if (dialogType == SettingsDialog.Backup) {
        SettingsBackupBottomSheet(
            onDismiss = { dialogType = SettingsDialog.None },
            onConfirm = { dialogType = SettingsDialog.None },
            backupState = backupState
        )
    }

    if (dialogType == SettingsDialog.Restore) {
        SettingsRestoreBottomSheet(
            onDismiss = {
                if (backupState is BackupState.Success)
                    requestRestart()
                else
                dialogType = SettingsDialog.None
                        },
            onConfirm = {
                if (backupState is BackupState.Success)
                    requestRestart()
                else
                    dialogType = SettingsDialog.None
            },
            backupState = backupState
        )
    }

    if (dialogType == SettingsDialog.DeleteAll) {
        SettingsDeleteAllBottomSheet(
            onDismiss = {
                if (isDataDeleted)
                    requestRestart()
                else
                    dialogType = SettingsDialog.None
            },
            onConfirm = {
                if (isDataDeleted)
                    requestRestart()
                else
                    requestDeleteData()
            },
            isLoading = isLoading,
            isDone = isDataDeleted
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Settings", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onClickBack() },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back to home",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = 4.dp,
                            bottomStart = 4.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Booklet",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Button(
                    onClick = {
                        uriHandler.openUri("https://GitHub.com/alibardide-dev/Booklet")
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_github),
                        contentDescription = "Open GitHub",
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                    .clickable {
                        if (updateStatus != UpdateStatus.CHECKING) {
                            if (updateStatus == UpdateStatus.AVAILABLE) {
                                uriHandler.openUri("https://github.com/alibardide-dev/Booklet/releases/latest")
                            } else {
                                onClickUpdate()
                            }
                        }
                    }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgedBox(
                    badge = {
                        if (updateStatus == UpdateStatus.AVAILABLE)
                            Badge()
                    }
                ) {
                    Crossfade(updateStatus == UpdateStatus.AVAILABLE) { target ->
                        if (target) {
                            CircularProgressIndicator(Modifier.size(24.dp))
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_sync),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text =
                            if (updateStatus == UpdateStatus.AVAILABLE)
                                "New Version Available: $nextUpdateVersion"
                            else if (BuildConfig.VERSION_NAME >= nextUpdateVersion)
                                "You're up to date"
                            else if (updateStatus == UpdateStatus.FAILED)
                                "Failed to check updates"
                            else
                                "Check for Updates",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = 4.dp,
                            bottomStart = 4.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            ) {
                Text(
                    text = "A",
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.onTertiaryContainer)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Ali Bardide",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = "Developer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Backup and Restore",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                    .clickable {
                        requestBackup()
                        dialogType = SettingsDialog.Backup
                    }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Backup",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Save your books data in your phone",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                    .clickable {
                        requestRestore()
                        dialogType = SettingsDialog.Restore
                    }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_hard_disk),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Restore",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Restore your previous data from a backup file",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .alpha(.4f)
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cloud_backup),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Cloud Save",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Not Available Now",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Danger Zone",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { dialogType = SettingsDialog.DeleteAll }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Delete All Data",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Remove EVERYTHING in Booklet",
                        fontSize = 14.sp,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}
