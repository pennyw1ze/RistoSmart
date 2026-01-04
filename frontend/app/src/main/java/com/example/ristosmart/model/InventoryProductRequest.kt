package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryProductRequest(
    // Required fields (*)
    val ean: String,
    val name: String,
    val price: Double,
    val quantity: Int,

    // Optional fields
    val category: String? = null,
    val description: String? = null,

    @SerialName("image_url")
    val imageUrl: String? = null

)
