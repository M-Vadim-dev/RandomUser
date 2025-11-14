package com.example.randomuser.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuser.domain.error.UserDetailError
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import com.example.randomuser.navigation.NavArgs.USER_ID
import com.example.randomuser.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val repository: UserRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val userId: String = savedStateHandle.get<String>(USER_ID) ?: ""

    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState

    init {
        loadUserFromDb()
    }

    private fun loadUserFromDb() {
        if (userId.isBlank()) {
            _uiState.value = UiState.Error(UserDetailError.IdNotProvided)
            return
        }

        viewModelScope.launch {
            try {
                val userFromDb = repository.getById(userId)
                _uiState.value =
                    if (userFromDb != null) UiState.Success(userFromDb) else UiState.Empty
            } catch (_: Exception) {
                _uiState.value = UiState.Error(UserDetailError.LoadFailed)
            }
        }
    }

}
