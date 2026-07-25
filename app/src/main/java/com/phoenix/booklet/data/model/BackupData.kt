package com.phoenix.booklet.data.model

import com.phoenix.booklet.utils.Constants

data class BackupData(
    val version: Int = Constants.DB_VERSION,
    val timestamp: Long,
    val books: List<Book>
)

sealed class BackupState {
    object Idle : BackupState()
    data class InProgress(val progress: Int) : BackupState()
    data class Success(val message: String? = null) : BackupState()
    data class Error(val error: String? = null) : BackupState()
}