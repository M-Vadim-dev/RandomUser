package com.example.randomuser.di

import com.example.randomuser.data.local.dao.UserDao
import com.example.randomuser.data.remote.RandomUserApi
import com.example.randomuser.data.repository.UserRepositoryImpl
import com.example.randomuser.di.qualifier.IoDispatcher
import com.example.randomuser.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        api: RandomUserApi,
        dao: UserDao,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): UserRepository = UserRepositoryImpl(api, dao, ioDispatcher)
}
