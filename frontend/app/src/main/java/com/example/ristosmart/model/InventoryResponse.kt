package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryResponse(
    val count: Int,
    val data: List<InventoryItem>,
    val success: Boolean
)

@Serializable
data class InventoryItem(
    val category: String,
    @SerialName("created_at")
    val createdAt: String,
    val description: String?, // Nullable since sample is "string" which might imply optional
    val ean: String,
    val id: String,
    @SerialName("image_url")
    val imageUrl: String?, // Nullable since sample is "string"
    val name: String,
    val price: Double,
    @SerialName("updated_at")
    val updatedAt: String
)