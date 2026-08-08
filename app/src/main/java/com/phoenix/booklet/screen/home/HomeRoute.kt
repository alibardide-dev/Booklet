package com.phoenix.booklet.screen.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSettings: () -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val books by homeViewModel.books.collectAsStateWithLifecycle()


    HomeScreen(
        onClickSettings = { navigateToSettings() },
        onSearchQueryChange = { homeViewModel.onAction(HomeUiActions.OnSearchQueryChange(it)) },
        isUpdateAvailable = uiState.isUpdateAvailable,
        isLoading = uiState.isLoading,
        books = books.sortedBy { it.dateUpdated }.reversed(),
        requestInsert = { homeViewModel.onAction(HomeUiActions.InsertBook(it)) },
        requestUpdate = { homeViewModel.onAction(HomeUiActions.UpdateBook(it)) },
        requestDelete = { homeViewModel.onAction(HomeUiActions.DeleteBooks(it)) },
        toggleFavorite = { homeViewModel.onAction(HomeUiActions.ToggleFavorite(it)) }
    )
}