package com.example.ristosmart.model

import kotlinx.serialization.Serializable

@Serializable
data class OrdersListResponse(
    val success: Boolean,
    val count: Int? = 0,
    val data: List<Order> = emptyList()
)
