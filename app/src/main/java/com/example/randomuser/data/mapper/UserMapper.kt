package com.example.randomuser.data.mapper

import com.example.randomuser.data.local.dbo.UserEntity
import com.example.randomuser.data.remote.dto.UserDto
import com.example.randomuser.domain.model.User
import com.example.randomuser.utils.DateUtils

fun UserDto.toEntity(): UserEntity = UserEntity(
    uuid = login?.uuid ?: "",
    firstName = name?.first,
    lastName = name?.last,
    email = email,
    phone = phone,
    gender = gender,
    nat = nat,
    largePicture = picture?.large,
    thumbnail = picture?.thumbnail,
    dob = DateUtils.formatDob(dob?.date),
    age = dob?.age.toString(),
    location = listOfNotNull(location?.country, location?.city).joinToString(", ")
)

fun UserEntity.toDomain() = User(
    uid = uuid,
    fullName = listOfNotNull(firstName, lastName).joinToString(" "),
    email = email,
    phone = phone,
    gender = gender,
    nat = nat,
    picture = largePicture,
    thumbnail = thumbnail,
    dob = dob,
    age = age,
    location = location
)
