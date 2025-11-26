package com.phoenix.booklet.screen.home

import com.phoenix.booklet.data.model.Book
import java.util.UUID

sealed interface HomeUiActions {
    data object DismissDialog: HomeUiActions
    data object InsertBookDialog: HomeUiActions
    data class UpdateBookDialog(val id: UUID): HomeUiActions
    data class InsertBook(val book: Book): HomeUiActions
    data class UpdateBook(val book: Book): HomeUiActions
    data class ApplyFilter(val filter: FilterStatus): HomeUiActions
}