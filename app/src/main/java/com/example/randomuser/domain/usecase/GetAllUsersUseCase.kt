package com.example.randomuser.domain.usecase

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): List<User> = repository.getAllUsers()
}