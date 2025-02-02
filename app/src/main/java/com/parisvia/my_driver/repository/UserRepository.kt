package com.parisvia.my_driver.repository

import com.parisvia.my_driver.model.LoginResponse
import com.parisvia.my_driver.network.ApiService
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiService.login(email, password)
    }
}
