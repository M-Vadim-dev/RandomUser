package com.example.randomuser.data.remote

import com.example.randomuser.data.remote.dto.ApiResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {
    @GET("api/")
    suspend fun getRandomUser(
        @Query("results") results: Int,
        @Query("gender") gender: String? = null,
        @Query("nat") nat: String? = null,
    ): ApiResponseDto
}