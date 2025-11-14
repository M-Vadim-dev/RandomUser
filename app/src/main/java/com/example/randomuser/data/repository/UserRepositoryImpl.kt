package com.example.randomuser.data.repository

import com.example.randomuser.data.local.dao.UserDao
import com.example.randomuser.data.mapper.toDomain
import com.example.randomuser.data.mapper.toEntity
import com.example.randomuser.data.remote.RandomUserApi
import com.example.randomuser.di.qualifier.IoDispatcher
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: RandomUserApi,
    private val dao: UserDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun getRandomUser(
        gender: String?,
        nat: String?,
        results: Int
    ): Result<List<User>> = withContext(ioDispatcher) {
        runCatching {
            val response = api.getRandomUser(results = results, gender = gender, nat = nat)

            if (response.results.isEmpty()) throw IllegalStateException("Server returned empty result")

            val entities = response.results.map { it.toEntity() }
            dao.insertAll(entities)

            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(uuid: String): User? =
        withContext(ioDispatcher) {
            dao.getById(uuid).firstOrNull()?.toDomain()
        }

    override suspend fun deleteUser(uuid: String): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                dao.deleteUser(uuid)
            }
        }

    override fun observeUser(uuid: String): Flow<User?> =
        dao.getById(uuid).map { entity ->
            entity?.toDomain()
        }

    override suspend fun getAllUsers(): List<User> = withContext(ioDispatcher) {
        dao.getAllUsers().map { it.toDomain() }
    }

}