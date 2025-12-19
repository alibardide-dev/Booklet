package com.phoenix.booklet.data

sealed interface Result {
    data object Success: Result
    data class Error(val error: String? = null): Result
}

sealed interface FileResult {
    data class Success(val filePath: String): FileResult
    data class Error(val error: String?): FileResult
}