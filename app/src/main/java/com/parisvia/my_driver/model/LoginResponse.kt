package com.parisvia.my_driver.model

data class LoginResponse(
    val user: User,
    val token: String
)

data class AppUser(
    val id: Int,
    val name: String,
    val email: String,
    val token: String
)
