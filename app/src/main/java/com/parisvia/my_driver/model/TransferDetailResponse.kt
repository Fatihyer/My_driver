package com.parisvia.my_driver.model

data class TransferDetailResponse(
    val id: Int,
    val start_date: String,
    val status: String?,
    val from: String,
    val to: String
)

