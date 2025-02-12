package com.parisvia.my_driver.model

import com.google.gson.annotations.SerializedName

data class StartMissionRequest(
    @SerializedName("depart_km") val departKm: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)
