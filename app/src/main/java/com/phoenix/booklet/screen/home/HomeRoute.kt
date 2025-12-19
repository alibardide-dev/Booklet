package com.phoenix.booklet.screen.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phoenix.booklet.screen.home.component.BookDetailsBottomSheet
import com.phoenix.booklet.screen.home.component.InsertBookBottomSheet
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSettings: () -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val books by homeViewModel.books.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun closeDialog() {
        coroutineScope.launch {
            sheetState.hide()
            homeViewModel.onAction(HomeUiActions.DismissDialog)
        }
    }

    fun openInsertDialog() {
        coroutineScope.launch {
            homeViewModel.onAction(HomeUiActions.InsertBookDialog)
            sheetState.show()
        }
    }

    fun openUpdateDialog(id: UUID) {
        coroutineScope.launch {
            homeViewModel.onAction(HomeUiActions.UpdateBookDialog(id))
            sheetState.show()
        }
    }

    fun openDetailsDialog(id: UUID) {
        coroutineScope.launch {
            homeViewModel.onAction(HomeUiActions.DetailsDialog(id))
            sheetState.show()
        }
    }

    HomeScreen(
        onClickSettings = { navigateToSettings() },
        isLoading = uiState.isLoading,
        books = books.sortedBy { it.dateUpdated }.reversed(),
        onClickBook = { openDetailsDialog(it) },
        onClickAdd = { openInsertDialog() },
        selectedFilter = uiState.selectedFilter,
        onSelectFilter = { homeViewModel.onAction(HomeUiActions.ApplyFilter(it)) }
    )

    when(uiState.dialogType) {
        HomeDialog.None -> Unit

        HomeDialog.Insert ->
            ModalBottomSheet(
                onDismissRequest = {},
                sheetState = sheetState,
                properties = ModalBottomSheetProperties(
                    shouldDismissOnBackPress = false,
                    shouldDismissOnClickOutside = false
                ),
                sheetGesturesEnabled = false,
                dragHandle = {},
            ) {
                InsertBookBottomSheet(
                    modifier = Modifier.fillMaxWidth(),
                    onClickClose = { closeDialog() },
                    onClickSave = {
                        coroutineScope.launch {
                            homeViewModel.onAction(HomeUiActions.InsertBook(it))
                            closeDialog()
                        }
                    }
                )
            }

        is HomeDialog.Update ->
            ModalBottomSheet(
                onDismissRequest = {},
                sheetState = sheetState,
                properties = ModalBottomSheetProperties(
                    shouldDismissOnBackPress = false,
                    shouldDismissOnClickOutside = false
                ),
                sheetGesturesEnabled = false,
                dragHandle = {},
            ) {
                val id = remember { (uiState.dialogType as HomeDialog.Update).id }
                val book = remember { books.first { it.id == id } }
                InsertBookBottomSheet(
                    modifier = Modifier.fillMaxWidth(),
                    onClickClose = { closeDialog() },
                    onClickSave = {
                        coroutineScope.launch {
                            homeViewModel.onAction(HomeUiActions.UpdateBook(it))
                            closeDialog()
                        }
                    },
                    book = book
                )
            }
        is HomeDialog.Details ->
            ModalBottomSheet(
                onDismissRequest = { closeDialog() },
                sheetState = sheetState
            ) {
                val id = remember { (uiState.dialogType as HomeDialog.Details).id }
                val book = remember { books.first { it.id == id } }
                BookDetailsBottomSheet(
                    modifier = Modifier.fillMaxWidth(),
                    book = book,
                    onClickEdit = {
                        closeDialog()
                        openUpdateDialog(id)
                    }
                )
            }
    }
}