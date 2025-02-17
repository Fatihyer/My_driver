package com.parisvia.my_driver.model

import com.google.gson.annotations.SerializedName

data class MissionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("mission") val mission: Mission?,
    @SerializedName("transfert") val transfert: Transfert?
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
    @SerializedName("finish_km") val finishKm: Int?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("start_user_id") val startUserId: Int?,
    @SerializedName("cleaning_status") val cleaningStatus: Int?
)

data class Transfert(
    @SerializedName("id") val id: Int,
    @SerializedName("from") val from: String,
    @SerializedName("target") val target: String,
    @SerializedName("surplace") val pecsurplace: String,
    @SerializedName("pax") val pax: Int,
    @SerializedName("vehicule") val vehicule: String,
    @SerializedName("ofis_start") val ofisStart: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("mission_url") val missionUrl: String?,
    @SerializedName("servicetype") val serviceType: String,
    @SerializedName("guzergah") val guzergah: List<Guzergah>,

)
data class Guzergah(
    val id: Int,
    val details: String
)


