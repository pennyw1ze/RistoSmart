package com.example.ristosmart.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OrderResponse(
    val success: Boolean,
    val message: String? = null,
    val data: JsonElement? = null
)
