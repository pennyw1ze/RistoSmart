package com.example.ristosmart.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderStatusRequest(
    val status: String
)
