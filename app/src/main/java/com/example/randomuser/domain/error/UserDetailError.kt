package com.example.randomuser.domain.error

sealed interface UserDetailError {
    object IdNotProvided : UserDetailError
    object LoadFailed : UserDetailError
}
