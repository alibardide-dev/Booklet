package com.phoenix.booklet.utils

sealed interface Result {
    data object Success: Result
    data class Error(val error: String? = null): Result
}

sealed interface FileResult {
    data class Success(val filePath: String): FileResult
    data class Error(val error: String?): FileResult
}

sealed interface UpdateResult {
    data class Success(val isUpdateAvailable: Boolean, val nextUpdateVersion: String): UpdateResult
    data class Error(val error: String?): UpdateResult
}