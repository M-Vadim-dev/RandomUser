package com.example.randomuser.data.local.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uuid: String,
    val firstName: String?,
    val lastName: String?,
    val dob: String?,
    val age: String?,
    val phone: String?,
    val email: String?,
    val gender: String?,
    val location: String?,
    val nat: String?,
    val thumbnail: String?,
    val largePicture: String?,
)