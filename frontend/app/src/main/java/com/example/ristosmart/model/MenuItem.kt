package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MenuItem(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    val price: Double,
    @SerialName("tax_amount")
    val taxAmount: Double? = null,
    val category: String,
    @SerialName("is_available")
    val isAvailable: Boolean,
    @SerialName("preparation_time")
    val preparationTime: Int,
    val allergens: List<String>? = null,
    @SerialName("nutritional_info")
    val nutritionalInfo: JsonObject? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)
