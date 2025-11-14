package com.example.randomuser.ui.screens.list

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.randomuser.domain.error.UserListError
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.usecase.DeleteUserUseCase
import com.example.randomuser.domain.usecase.GetAllUsersUseCase
import com.example.randomuser.domain.usecase.GetRandomUserUseCase
import com.example.randomuser.navigation.NavArgs.GENDER
import com.example.randomuser.navigation.NavArgs.NAT
import com.example.randomuser.utils.NetworkChecker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class UserListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getRandomUserUseCase: GetRandomUserUseCase
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase
    private lateinit var deleteUserUseCase: DeleteUserUseCase
    private lateinit var networkChecker: NetworkChecker

    private lateinit var viewModel: UserListViewModel

    private val sampleUser = User(
        uid = "1",
        fullName = "Maksimets Vadim",
        email = "Vadim@example.com",
        phone = "+7 978 7612216",
        gender = "male",
        nat = "US",
        picture = "",
        thumbnail = "",
        dob = "03.05.1989",
        age = "36",
        location = "Russia, Moscow"
    )

    @BeforeEach
    fun setUp() {
        getRandomUserUseCase = mockk()
        getAllUsersUseCase = mockk()
        deleteUserUseCase = mockk()
        networkChecker = mockk()
    }

    private fun createViewModel(
        gender: String? = null,
        nat: String? = null
    ): UserListViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                GENDER to (gender ?: ""),
                NAT to (nat ?: "")
            )
        )
        return UserListViewModel(
            getRandomUserUseCase,
            getAllUsersUseCase,
            deleteUserUseCase,
            networkChecker,
            savedStateHandle
        )
    }

    @Test
    fun `init loads users from DB successfully`() = runTest(testDispatcher) {
        coEvery { getAllUsersUseCase() } returns listOf(sampleUser)
        every { networkChecker.isOnline() } returns true
        coEvery { getRandomUserUseCase(any(), any(), any()) } returns Result.success(
            listOf(
                sampleUser
            )
        )

        viewModel = createViewModel()

        viewModel.users.test {
            advanceUntilIdle()
            val emission = awaitItem()
            assertEquals(listOf(sampleUser), emission)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `addRandomUser is called in init and emits user`() = runTest(testDispatcher) {
        coEvery { getAllUsersUseCase() } returns emptyList()
        every { networkChecker.isOnline() } returns true
        coEvery { getRandomUserUseCase(any(), any(), any()) } returns Result.success(
            listOf(
                sampleUser
            )
        )

        viewModel = createViewModel()

        viewModel.users.test {
            advanceUntilIdle()

            val emission = awaitItem()
            assertEquals(listOf(sampleUser), emission)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `addRandomUser sets error when no internet`() = runTest(testDispatcher) {
        coEvery { getAllUsersUseCase() } returns emptyList()
        every { networkChecker.isOnline() } returns false

        viewModel = createViewModel()

        viewModel.error.test {
            advanceUntilIdle()
            awaitItem()
            val errorEmission = awaitItem()
            assertEquals(UserListError.NoInternet, errorEmission)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `deleteUser removes user successfully`() = runTest(testDispatcher) {
        coEvery { getAllUsersUseCase() } returns listOf(sampleUser)
        every { networkChecker.isOnline() } returns true
        coEvery { getRandomUserUseCase(any(), any(), any()) } returns Result.success(emptyList())
        coEvery { deleteUserUseCase("1") } returns Result.success(Unit)

        viewModel = createViewModel()

        viewModel.users.test {
            advanceUntilIdle()
            val initial = awaitItem()
            assertEquals(listOf(sampleUser), initial)

            viewModel.deleteUser("1")
            advanceUntilIdle()
            val afterDelete = awaitItem()
            assertEquals(emptyList<User>(), afterDelete)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `deleteUser sets error when deletion fails`() = runTest(testDispatcher) {
        coEvery { getAllUsersUseCase() } returns listOf(sampleUser)
        every { networkChecker.isOnline() } returns true
        coEvery { getRandomUserUseCase(any(), any(), any()) } returns Result.success(emptyList())
        coEvery { deleteUserUseCase("1") } returns Result.failure(Exception("fail"))

        viewModel = createViewModel()

        viewModel.error.test {
            advanceUntilIdle()
            awaitItem()

            viewModel.deleteUser("1")
            advanceUntilIdle()
            val errorEmission = awaitItem()
            assertEquals(UserListError.DeleteFailed, errorEmission)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshUsersFromDb updates users correctly`() = runTest(testDispatcher) {
        every { networkChecker.isOnline() } returns true
        coEvery { getRandomUserUseCase(any(), any(), any()) } returns Result.success(emptyList())
        coEvery { getAllUsersUseCase() } returnsMany listOf(
            emptyList(),
            listOf(sampleUser)
        )

        viewModel = createViewModel()

        viewModel.users.test {
            advanceUntilIdle()
            val initial = awaitItem()
            assertEquals(emptyList<User>(), initial)

            viewModel.refreshUsersFromDb()
            advanceUntilIdle()
            val updated = awaitItem()
            assertEquals(listOf(sampleUser), updated)

            cancelAndConsumeRemainingEvents()
        }
    }
}
