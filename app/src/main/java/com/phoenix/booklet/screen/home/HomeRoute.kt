package com.phoenix.booklet.screen.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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

    fun openDeleteDialog(ids: List<UUID>) {
        coroutineScope.launch {
            homeViewModel.onAction(HomeUiActions.DeleteDialog(ids))
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
                    },
                    onClickDelete = {
                        closeDialog()
                        openDeleteDialog(listOf(id))
                    }
                )
            }

        is HomeDialog.Delete -> {
            val ids = (uiState.dialogType as HomeDialog.Delete).ids
            AlertDialog(
                onDismissRequest = { closeDialog() },
                title = { Text("Delete Book") },
                text = {
                    Text(
                        text = if (ids.size > 1) "Are you sure you want to delete ${ids.size} books?" else "Are you sure you want to delete this book?"
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            closeDialog()
                            homeViewModel.onAction(HomeUiActions.DeleteBooks(ids))
                                  },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        Text("Yes, Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { closeDialog() },
                        enabled = !uiState.isLoading
                    ) {
                        Text("No, Abort")
                    }
                }
            )
        }
    }
}