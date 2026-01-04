package com.example.ristosmart.model

import kotlinx.serialization.Serializable

@Serializable
data class InventoryUpdateRequest(
    val amount: Int,
    val operation: String
)

@Serializable
data class InventoryPutResponse(
    val data: InventoryItem,
    val message: String,
    val success: Boolean
)