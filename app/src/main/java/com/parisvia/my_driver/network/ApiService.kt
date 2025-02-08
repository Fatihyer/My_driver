package com.parisvia.my_driver.network

import com.parisvia.my_driver.model.LoginResponse
import com.parisvia.my_driver.model.TransferDetailResponse
import com.parisvia.my_driver.model.TransferResponse
import com.parisvia.my_driver.model.StatusUpdateRequest
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.Body

interface ApiService {

    @FormUrlEncoded
    @POST("api/login") // Laravel'deki login endpoint'i
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("api/transfers") // Laravel'deki Transfer API endpoint'i
    suspend fun getTransfers(
        @Header("Authorization") token: String, // Kullanıcı token'ı
        @Query("start_date") dateOption: String? // "today", "yesterday", "tomorrow" gibi filtreler
    ): Response<TransferResponse>
    @GET("api/transfer/{id}")
    suspend fun getTransferDetail(
        @Header("Authorization") token: String,
        @Path("id") transferId: Int
    ): Response<TransferDetailResponse>

    @PUT("api/confirmTransfer/{id}")
    suspend fun confirmTransfer(
        @Header("Authorization") token: String,
        @Path("id") transferId: Int,
        @Body request: StatusUpdateRequest
    ): Response<Unit>

}
