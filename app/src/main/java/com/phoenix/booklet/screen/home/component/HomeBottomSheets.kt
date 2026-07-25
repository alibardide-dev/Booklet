package com.phoenix.booklet.screen.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.screen.AlertBottomSheetTemplate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun HomeDetailBottomSheet(
    book: Book,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        BookDetailsBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            book = book,
            onClickEdit = {
                scope.launch {
                    sheetState.hide()
                    onEdit()
                }
                          },
            onClickDelete = {
                scope.launch {
                    sheetState.hide()
                    onDelete()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeInsertBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Book) -> Unit,
    book: Book? = null
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false,
            shouldDismissOnClickOutside = false
        ),
        sheetGesturesEnabled = false,
    ) {
        InsertBookBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            onClickClose = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            onClickSave = {
                scope.launch {
                    sheetState.hide()
                    onSave(it)
                    onDismiss()
                }
            },
            book = book
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDeleteBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    size: Int,
    isLoading: Boolean,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val title = remember {
        if (size > 1) "Delete Books"
        else "Delete Book"
    }
    val message = remember {
        if (size > 1) "Are you sure you want to delete $size books?"
        else "Are you sure you want to delete this book?"
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        AlertBottomSheetTemplate(
            title = title,
            message = message,
            confirmText = "Yes, Delete",
            dismissText = "No, Abort",
            isLoading = isLoading,
            onConfirm = {
                scope.launch {
                    sheetState.hide()
                    onConfirm()
                }
            },
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            confirmButtonColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
            isDismissAllowed = !isLoading
        )
    }
}
