package com.parisvia.my_driver.model

import com.parisvia.my_driver.model.Transfer // Transfer'i içe aktar

data class TransferDetailResponse(
    val success: Boolean,
    val transfer: Transfer,
    val misafirler: List<Misafir>, // Birden fazla misafir için liste ekledik
    val trajets: List<Trajet> // Yeni eklenen trajets modeli
)

data class Misafir(
    val name: String,
    val surname: String,
    val phone: String,
    val whatsapp_link: String?
)


data class Trajet(
    val id: Int,
    val details: String
)