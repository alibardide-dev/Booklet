package com.phoenix.booklet.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
    onNavigateBack: () -> Unit,
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        onClickBack = { onNavigateBack() }
    )
}