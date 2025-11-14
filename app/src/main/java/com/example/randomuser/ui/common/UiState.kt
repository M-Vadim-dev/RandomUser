package com.example.randomuser.ui.common

import com.example.randomuser.domain.error.UserDetailError

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: UserDetailError) : UiState<Nothing>
    object Empty : UiState<Nothing>
}
