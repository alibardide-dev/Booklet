package com.phoenix.booklet.screen.home

import com.phoenix.booklet.data.model.Book
import java.util.UUID

sealed interface HomeUiActions {
    data class OnSearchQueryChange(val query: String): HomeUiActions
    data class InsertBook(val book: Book): HomeUiActions
    data class UpdateBook(val book: Book): HomeUiActions
    data class ApplyFilter(val filter: FilterStatus): HomeUiActions
    data class DeleteBooks(val ids: List<UUID>): HomeUiActions
    data class ToggleFavorite(val id: UUID): HomeUiActions

}