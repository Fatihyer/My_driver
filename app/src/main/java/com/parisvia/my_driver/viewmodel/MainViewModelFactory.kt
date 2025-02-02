package com.parisvia.my_driver.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parisvia.my_driver.network.ApiClient
import com.parisvia.my_driver.repository.UserRepository

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = ApiClient.apiService
        val userRepository = UserRepository(apiService)
        return MainViewModel(userRepository) as T
    }
}