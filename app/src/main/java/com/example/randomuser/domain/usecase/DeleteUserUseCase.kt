package com.example.randomuser.domain.usecase

import com.example.randomuser.domain.repository.UserRepository
import javax.inject.Inject


class DeleteUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(uuid: String): Result<Unit> = repository.deleteUser(uuid)

}
