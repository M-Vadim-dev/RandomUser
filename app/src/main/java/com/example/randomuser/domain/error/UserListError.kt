package com.example.randomuser.domain.error

sealed interface UserListError {
    object NoInternet : UserListError
    object FetchFailed : UserListError
    object LoadFailed : UserListError
    object DeleteFailed : UserListError
}
