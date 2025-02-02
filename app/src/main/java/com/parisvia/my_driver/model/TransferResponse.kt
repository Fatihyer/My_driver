package com.parisvia.my_driver.model


data class TransferResponse(
    val success: Boolean,
    val transfers: List<Transfer>
)
