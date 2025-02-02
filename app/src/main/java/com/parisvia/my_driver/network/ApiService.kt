package com.parisvia.my_driver.network

import com.parisvia.my_driver.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("api/login") // Laravel'deki login endpoint'i
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ):  Response<LoginResponse>
}
