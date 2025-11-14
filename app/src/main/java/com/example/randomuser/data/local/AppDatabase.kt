package com.example.randomuser.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.randomuser.data.local.dao.UserDao
import com.example.randomuser.data.local.dbo.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}