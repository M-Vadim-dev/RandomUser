package com.example.randomuser.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.randomuser.data.local.dbo.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE uuid = :uuid LIMIT 1")
    fun getById(uuid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("DELETE FROM users WHERE uuid = :uuid")
    suspend fun deleteUser(uuid: String)

}