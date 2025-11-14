package com.example.randomuser.ui.screens.list

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuser.domain.error.UserListError
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.usecase.DeleteUserUseCase
import com.example.randomuser.domain.usecase.GetAllUsersUseCase
import com.example.randomuser.domain.usecase.GetRandomUserUseCase
import com.example.randomuser.navigation.NavArgs.GENDER
import com.example.randomuser.navigation.NavArgs.NAT
import com.example.randomuser.utils.NetworkChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val networkChecker: NetworkChecker,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val gender: String? = savedStateHandle.get<String>(GENDER)?.ifBlank { null }
    private val nat: String? = savedStateHandle.get<String>(NAT)?.ifBlank { null }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _error = MutableStateFlow<UserListError?>(null)
    val error: StateFlow<UserListError?> = _error

    init {
        loadUsersFromDb()
        addRandomUser()
    }

    fun addRandomUser(results: Int = 1) {
        viewModelScope.launch {
            if (!networkChecker.isOnline()) {
                _error.value = UserListError.NoInternet
                return@launch
            }

            _isLoadingMore.value = true
            getRandomUserUseCase(gender, nat, results).onSuccess { newUsers ->
                _users.value += newUsers
            }.onFailure { throwable ->
                Log.e("UserListViewModel", "Fetch failed", throwable)
                _error.value = UserListError.FetchFailed
            }
            _isLoadingMore.value = false
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            deleteUserUseCase(userId).onSuccess {
                _users.value = _users.value.filterNot { it.uid == userId }
            }.onFailure {
                _error.value = UserListError.DeleteFailed
            }
        }
    }

    fun refreshUsersFromDb() {
        loadUsersFromDb()
    }

    private fun loadUsersFromDb() {
        viewModelScope.launch {
            try {
                val dbUsers = getAllUsersUseCase()
                _users.value = dbUsers
            } catch (_: Exception) {
                _error.value = UserListError.LoadFailed
            }
        }
    }

}
