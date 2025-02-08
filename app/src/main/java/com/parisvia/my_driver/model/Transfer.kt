package com.parisvia.my_driver.model

data class Transfer(
    val id: Int,
    val post_id: Int?,
    val start_date: String,
    val ofis_start: String,
    val surplace: String,
    val servicetype_id: Int?,
    val from: String,
    val target: String,
    val pax: String,
    val status_id: Int?,
    val comments: String?,
    val dcomments: String?,
    val servicetype: ServiceType,
    val status: Status,
    val vehicule: Vehicule,
    val timetable: Timetable

)
data class ServiceType(
    val id: Int,
    val name: String
)

data class Status(
    val id: Int,
    val name: String // Status bilgisini JSON'dan alacağız
)


data class Vehicule(
    val id: Int,
    val name: String // Status bilgisini JSON'dan alacağız
)

data class Timetable(
    val ofisStart: String,
    val sur_place: String,
    val startDate: String,
    val endDate: String
)