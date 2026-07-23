package com.phoenix.booklet.screen.home

import java.util.UUID

data class HomeUiState(
    val isLoading: Boolean = false,
    val topBarStatus: TopBarStatus = TopBarStatus.Normal,
    val searchQuery: String = "",
    val selectedFilter: FilterStatus = FilterStatus.ALL,
    val isUpdateAvailable: Boolean = false,
)

enum class FilterStatus {
    ALL, WISHLIST, READING, FINISHED, ARCHIVED
}

enum class TopBarStatus {
    Normal, Search, Select
}

sealed interface HomeDialog {
    data object None: HomeDialog
    data object Insert: HomeDialog
    data class Update(val id: UUID): HomeDialog
    data class Details(val id: UUID): HomeDialog
    data class Delete(val ids: List<UUID>): HomeDialog
}