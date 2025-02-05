package com.parisvia.my_driver.model

import com.google.gson.annotations.SerializedName

data class TransferDetailResponse(
    @SerializedName("start_date") val startDate: String // JSON'daki alanla eşleşmeli
)
