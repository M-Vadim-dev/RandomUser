package com.example.randomuser.domain.repository

import com.example.randomuser.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getRandomUser(gender: String?, nat: String?, results: Int): Result<List<User>>

    suspend fun getById(uuid: String): User?

    suspend fun getAllUsers(): List<User>

    fun observeUser(uuid: String): Flow<User?>

    suspend fun deleteUser(uuid: String): Result<Unit>
}
