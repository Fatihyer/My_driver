package com.parisvia.my_driver.model

data class TransferDetailResponse(
    val success: Boolean,
    val transfer: Transfer
)

data class Transfer(
    val id: Int,
    val start_date: String,
    val from: String,
    val to: String,
    val servicetype: ServiceType,
    val status: Status
)

data class ServiceType(
    val id: Int,
    val name: String
)

data class Status(
    val id: Int,
    val name: String
)