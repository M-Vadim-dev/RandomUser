package com.example.randomuser.domain.usecase

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import javax.inject.Inject

class GetRandomUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(
        gender: String?,
        nat: String?,
        count: Int = DEFAULT_RANDOM_USER_COUNT,
    ): Result<List<User>> =
        repository.getRandomUser(gender = gender, nat = nat, results = count)

    companion object {
        private const val DEFAULT_RANDOM_USER_COUNT: Int = 1
    }
}
