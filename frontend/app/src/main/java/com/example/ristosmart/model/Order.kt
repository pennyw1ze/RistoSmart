package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    @SerialName("order_number")
    val orderNumber: String,
    @SerialName("table_number")
    val tableNumber: Int? = null,
    @SerialName("customer_name")
    val customerName: String? = null,
    val status: String,
    @SerialName("order_type")
    val orderType: String,
    @SerialName("total_amount")
    val totalAmount: Double,
    @SerialName("tax_amount")
    val taxAmount: Double,
    @SerialName("discount_amount")
    val discountAmount: Double,
    @SerialName("final_amount")
    val finalAmount: Double,
    @SerialName("special_instructions")
    val specialInstructions: String? = null,
    @SerialName("estimated_completion_time")
    val estimatedCompletionTime: String? = null,
    val items: List<OrderItem> = emptyList(),
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)
