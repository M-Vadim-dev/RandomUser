package com.example.randomuser.domain.model

data class User(
    val uid: String,
    val gender: String?,
    val fullName: String,
    val dob: String?,
    val age: String?,
    val email: String?,
    val picture: String?,
    val thumbnail: String?,
    val phone: String?,
    val location: String?,
    val nat: String?,
)
