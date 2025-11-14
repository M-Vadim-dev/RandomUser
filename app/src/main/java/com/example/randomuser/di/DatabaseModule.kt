package com.example.randomuser.di

import android.content.Context
import androidx.room.Room
import com.example.randomuser.data.local.AppDatabase
import com.example.randomuser.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "randomuser_db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}
