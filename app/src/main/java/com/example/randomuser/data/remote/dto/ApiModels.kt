package com.example.randomuser.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto(
    val results: List<UserDto>,
)

@Serializable
data class UserDto(
    val gender: String? = null,
    val name: NameDto? = null,
    val location: LocationDto? = null,
    val email: String? = null,
    val login: LoginDto? = null,
    val dob: DobDto? = null,
    val phone: String? = null,
    val picture: PictureDto? = null,
    val nat: String? = null,
    val id: IdDto? = null,
)

@Serializable
data class IdDto(
    val name: String? = null,
    val value: String? = null,
)

@Serializable
data class NameDto(
    val title: String? = null,
    val first: String? = null,
    val last: String? = null,
)

@Serializable
data class LocationDto(
    val street: StreetDto? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
)

@Serializable
data class StreetDto(
    val number: Int? = null,
    val name: String? = null,
)

@Serializable
data class LoginDto(
    val uuid: String? = null,
    val username: String? = null,
)

@Serializable
data class DobDto(
    val date: String? = null,
    val age: Int? = null,
)

@Serializable
data class PictureDto(
    val large: String? = null,
    val medium: String? = null,
    val thumbnail: String? = null,
)
