package com.example.ristosmart.model

import kotlinx.serialization.Serializable

@Serializable
data class InventoryUpdateRequest(
    val quantity: Int
)