package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val items: List<OrderItemRequest>,
    @SerialName("order_type")
    val orderType: String = "dine_in",
    @SerialName("special_instructions")
    val specialInstructions: String? = null,
    @SerialName("table_number")
    val tableNumber: Int
)

@Serializable
data class OrderItemRequest(
    @SerialName("menu_item_id")
    val menuItemId: String,
    val quantity: Int
)
