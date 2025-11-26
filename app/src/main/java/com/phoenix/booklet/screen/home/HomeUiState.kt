package com.phoenix.booklet.screen.home

import java.util.UUID

data class HomeUiState(
    val isLoading: Boolean = false,
    val dialogType: HomeDialog = HomeDialog.None,
    val selectedFilter: FilterStatus = FilterStatus.ALL,
)

enum class FilterStatus {
    ALL, WISHLIST, READING, FINISHED, ARCHIVED
}

sealed interface HomeDialog {
    data object None: HomeDialog
    data object Insert: HomeDialog
    data class Update(val id: UUID): HomeDialog
    data class Details(val id: UUID): HomeDialog
}