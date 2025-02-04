package com.parisvia.my_driver.model

data class Transfer(
    val id: Int,
    val post_id: Int?,
    val start_date: String,
    val ofis_start: String,
    val servicetype_id: Int?,
    val from: String,
    val target: String,
    val status_id: Int?,
    val comments: String?,
    val dcomments: String?,
    val servicetype: ServiceType,
    val status: Status
)
data class ServiceType(
    val id: Int,
    val name: String
)

data class Status(
    val id: Int,
    val name: String // Status bilgisini JSON'dan alacağız
)