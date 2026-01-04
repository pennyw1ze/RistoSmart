package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: String,
    @SerialName("menu_item_id")
    val menuItemId: String,
    @SerialName("menu_item_name")
    val menuItemName: String,
    val quantity: Int,
    @SerialName("unit_price")
    val unitPrice: Double,
    @SerialName("total_price")
    val totalPrice: Double,
    @SerialName("special_instructions")
    val specialInstructions: String? = null,
    val status: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)
