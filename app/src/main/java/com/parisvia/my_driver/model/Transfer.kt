package com.parisvia.my_driver.model

data class Transfer(
    val id: Int,
    val post_id: Int?,
    val start_date: String,
    val servicetype_id: Int?,
    val from: String,
    val target: String,
    val status_id: Int?,
    val comments: String?,
    val dcomments: String?
)
