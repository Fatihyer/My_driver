// MainViewModel.kt
package com.parisvia.my_driver.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parisvia.my_driver.model.LoginResponse
import com.parisvia.my_driver.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> = _loginResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _networkError = MutableLiveData<String>()
    val networkError: LiveData<String> = _networkError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                } else {
                    _errorMessage.value = "Giriş başarısız: ${response.message()}"
                }
            } catch (e: Exception) {
                _networkError.value = "Ağ hatası: ${e.message}"
            }
        }
    }
}
