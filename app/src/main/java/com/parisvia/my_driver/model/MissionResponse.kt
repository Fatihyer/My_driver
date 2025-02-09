package com.parisvia.my_driver.model

import com.google.gson.annotations.SerializedName

data class MissionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("mission") val mission: Mission?,
    @SerializedName("transfer") val transfer: Transfer?
)

data class Mission(
    @SerializedName("id") val id: Int,
    @SerializedName("transfer_id") val transferId: Int,
    @SerializedName("hareket") val hareket: String?,
    @SerializedName("surplace") val surplace: String?,
    @SerializedName("taked") val taked: String?,
    @SerializedName("finish") val finish: String?,
    @SerializedName("finish_depot") val finishDepot: String?,
    @SerializedName("depart_km") val departKm: Int?,
    @SerializedName("finish_km") val finishKm: Int?
)
